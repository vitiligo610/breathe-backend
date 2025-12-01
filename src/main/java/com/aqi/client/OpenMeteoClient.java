package com.aqi.client;

import com.aqi.dto.geocoding.ReverseGeocodingResponse;
import com.aqi.dto.meteo.AirQualityResponse;
import com.aqi.dto.meteo.WeatherForecastResponse;
import com.aqi.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenMeteoClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OM_WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String OM_AIR_QUALITY_API_URL = "https://air-quality-api.open-meteo.com/v1/air-quality";
    private static final String REVERSE_GEO_URL = "https://api-bdc.io/data/reverse-geocode-client";

    private static final String WEATHER_PARAMS = "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m";
    private static final String WEATHER_PARAMS_DAILY = "temperature_2m_max,temperature_2m_min,weather_code,wind_speed_10m_max,wind_direction_10m_dominant,relative_humidity_2m_max";

    private static final String AQI_PARAMS = "us_aqi,pm10,pm2_5,ozone,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide";

    @Value("${app.open-meteo.summary-forecast-days:3}")
    private Integer summaryForecastDays;

    @Value("${app.open-meteo.forecast-days:5}")
    private Integer forecastDays;

    @Value("${app.open-meteo.past-days:30}")
    private Integer pastDays;

    public WeatherForecastResponse fetchWeather(Double latitude, Double longitude) {
        return fetchWeatherInternal(latitude, longitude, true);
    }

    public WeatherForecastResponse fetchWeatherSummary(Double latitude, Double longitude) {
        return fetchWeatherInternal(latitude, longitude, false);
    }

    public AirQualityResponse fetchAirQuality(Double latitude, Double longitude) {
        return fetchAirQualityInternal(latitude, longitude, true, false);
    }

    public AirQualityResponse fetchAirQualitySummary(Double latitude, Double longitude) {
        return fetchAirQualityInternal(latitude, longitude, false, false);
    }

    public AirQualityResponse fetchAirQualityHistory(Double latitude, Double longitude) {
        return fetchAirQualityInternal(latitude, longitude, false, true);
    }

    public AirQualityResponse[] fetchAirQualityBatch(List<Double> latitudes, List<Double> longitudes) {
        String latParam = latitudes.stream().map(String::valueOf).collect(Collectors.joining(","));
        String lonParam = longitudes.stream().map(String::valueOf).collect(Collectors.joining(","));

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OM_AIR_QUALITY_API_URL)
                .queryParam("latitude", latParam)
                .queryParam("longitude", lonParam)
                .queryParam("current", "us_aqi")
                .queryParam("timezone", "auto")
                .queryParam("timeformat", "unixtime");

        String url = builder.toUriString();
        log.info("Executing batch request: {}", url);

        if (latitudes.size() == 1) {
            AirQualityResponse singleResponse = executeRequest(url, AirQualityResponse.class);
            return new AirQualityResponse[]{ singleResponse };
        } else {
            return executeRequest(url, AirQualityResponse[].class);
        }
    }

    public ReverseGeocodingResponse fetchLocationName(Double latitude, Double longitude) {
        String url = UriComponentsBuilder.fromUriString(REVERSE_GEO_URL)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("localityLanguage", "en")
                .toUriString();

        try {
            return executeRequest(url, ReverseGeocodingResponse.class);
        } catch (Exception e) {
            log.warn("Failed to resolve location name: {}", e.getMessage());
            return new ReverseGeocodingResponse();
        }
    }

    private WeatherForecastResponse fetchWeatherInternal(
            Double latitude,
            Double longitude,
            Boolean isDetailedData
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OM_WEATHER_API_URL)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current", WEATHER_PARAMS)
                .queryParam("daily", WEATHER_PARAMS_DAILY)
                .queryParam("timezone", "auto")
                .queryParam("timeformat", "unixtime");

        if (isDetailedData) {
            builder.queryParam("hourly", WEATHER_PARAMS);
        } else {
            builder.queryParam("forecast_days", summaryForecastDays);
        }

        return executeRequest(builder.toUriString(), WeatherForecastResponse.class);
    }

    private AirQualityResponse fetchAirQualityInternal(
            Double latitude,
            Double longitude,
            Boolean isDetailedData,
            Boolean isPastData
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OM_AIR_QUALITY_API_URL)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("hourly", AQI_PARAMS)
                .queryParam("timezone", "auto")
                .queryParam("timeformat", "unixtime");

        if (!isPastData) {
            builder.queryParam("current", AQI_PARAMS);
        }

        if (isDetailedData) {
            builder.queryParam("forecast_days", forecastDays);
        } else if (isPastData) {
            builder.queryParam("past_days", pastDays);
            builder.queryParam("forecast_days", 0);
        } else {
            builder.queryParam("forecast_days", summaryForecastDays);
        }

        return executeRequest(builder.toUriString(), AirQualityResponse.class);
    }

    private <T> T executeRequest(String url, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.getForEntity(url, responseType);
            if (response.getBody() == null) {
                throw new ExternalApiException("Received empty body from OpenMeteo");
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching data from OpenMeteo: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch data: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching data: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch external data", e);
        }
    }
}
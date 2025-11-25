package com.aqi.client;

import com.aqi.dto.geocoding.ReverseGeocodingResponse;
import com.aqi.dto.meteo.AirQualityResponse;
import com.aqi.dto.meteo.WeatherForecastResponse;
import com.aqi.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public WeatherForecastResponse fetchWeather(Double latitude, Double longitude) {
        return fetchWeatherInternal(latitude, longitude, null, null);
    }

    public AirQualityResponse fetchAirQuality(Double latitude, Double longitude) {
        return fetchAirQualityInternal(latitude, longitude, null, null);
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

    private WeatherForecastResponse fetchWeatherInternal(Double lat, Double lon, Integer forecastDays, Integer pastDays) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OM_WEATHER_API_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("current", WEATHER_PARAMS)
                .queryParam("hourly", WEATHER_PARAMS)
                .queryParam("daily", WEATHER_PARAMS_DAILY)
                .queryParam("timezone", "auto")
                .queryParam("timeformat", "unixtime");

        if (forecastDays != null) builder.queryParam("forecast_days", forecastDays);
        if (pastDays != null) builder.queryParam("past_days", pastDays);

        return executeRequest(builder.toUriString(), WeatherForecastResponse.class);
    }

    private AirQualityResponse fetchAirQualityInternal(Double lat, Double lon, Integer forecastDays, Integer pastDays) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OM_AIR_QUALITY_API_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("current", AQI_PARAMS)
                .queryParam("hourly", AQI_PARAMS)
                .queryParam("timezone", "auto")
                .queryParam("timeformat", "unixtime");

        if (forecastDays != null) builder.queryParam("forecast_days", forecastDays);
        if (pastDays != null) builder.queryParam("past_days", pastDays);

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
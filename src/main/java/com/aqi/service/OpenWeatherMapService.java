package com.aqi.service;

import com.aqi.dto.aqi.AqiData;
import com.aqi.dto.aqi.WeatherData;
import com.aqi.dto.aqi.WindData;
import com.aqi.dto.location.LocationDataDto;
import com.aqi.dto.omw.AirPollutionResponse;
import com.aqi.dto.omw.WeatherResponse;
import com.aqi.exception.ExternalApiException;
import com.aqi.util.AqiCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class OpenWeatherMapService {

    private final AqiCalculator aqiCalculator;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.openweathermap.api-key}")
    private String apiKey;

    private static final String OWM_AQI_URL = "https://api.openweathermap.org/data/2.5/air_pollution";
    private static final String OWM_WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    public LocationDataDto getExternalLocationData(Double lat, Double lon) {
        log.info("Fetching location data. lat: {}, lon: {}", lat, lon);

        AirPollutionResponse aqiResponse = fetchAirPollutionData(lat, lon);

        if (aqiResponse == null || aqiResponse.getList() == null || aqiResponse.getList().isEmpty()) {
            throw new ExternalApiException("No AQI data found for location.");
        }

        AirPollutionResponse.AirPollutionEntry latestEntry = aqiResponse.getList().getFirst();

        AirPollutionResponse.Components components = latestEntry.getComponents();
        if (components == null) {
            throw new ExternalApiException("AQI data incomplete for location.");
        }

        Map<String, Double> pollutants = extractPollutants(components);

        AqiData aqiData = aqiCalculator.calculateAqi(pollutants);

        WeatherData weather = fetchWeatherData(lat, lon);

        return LocationDataDto.builder()
                .latitude(lat)
                .longitude(lon)
                .timestamp(Instant.now().getEpochSecond())
                .aqi(aqiData)
                .weather(weather)
                .build();
    }

    private AirPollutionResponse fetchAirPollutionData(Double lat, Double lon) {
        String url = UriComponentsBuilder.fromUriString(OWM_AQI_URL)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .toUriString();

        log.info("Fetching AQI data from: {}", url);

        try {
            ResponseEntity<AirPollutionResponse> response = restTemplate.getForEntity(url, AirPollutionResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching AQI data: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch AQI data: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching AQI data: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch AQI data", e);
        }
    }

    private WeatherData fetchWeatherData(double lat, double lon) {
        String url = UriComponentsBuilder.fromUriString(OWM_WEATHER_URL)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .toUriString();

        log.info("Fetching Weather data from: {}", url);

        try {
            ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(url, WeatherResponse.class);
            WeatherResponse body = response.getBody();

            if (body == null) {
                throw new ExternalApiException("No response body: " + response.getStatusCode());
            }

            return WeatherData.builder()
                    .temperatureC(body.getMain().getTemp())
                    .weatherIcon(body.getWeather().getFirst().getIcon())
                    .weatherDescription(body.getWeather().getFirst().getDescription())
                    .humidityPercent(body.getMain().getHumidity())
                    .wind(
                            WindData.builder()
                                    .speedMps(body.getWind().getSpeed())
                                    .angle(body.getWind().getDeg())
                                    .build()
                    )
                    .build();
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching weather data: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch weather data: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching weather data: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch weather data", e);
        }
    }

    private Map<String, Double> extractPollutants(AirPollutionResponse.Components components) {
        Map<String, Double> pollutants = new HashMap<>();

        pollutants.put("co", components.getCo());
        pollutants.put("no2", components.getNo2());
        pollutants.put("o3", components.getO3());
        pollutants.put("so2", components.getSo2());
        pollutants.put("pm2_5", components.getPm2_5());
        pollutants.put("pm10", components.getPm10());

        if (components.getNo() != null) pollutants.put("no", components.getNo());
        if (components.getNh3() != null) pollutants.put("nh3", components.getNh3());

        return pollutants;
    }
}

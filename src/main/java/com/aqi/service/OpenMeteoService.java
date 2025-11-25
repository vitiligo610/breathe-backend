package com.aqi.service;

import com.aqi.client.OpenMeteoClient;
import com.aqi.dto.geocoding.ReverseGeocodingResponse;
import com.aqi.dto.location.LocationClimateData;
import com.aqi.dto.location.LocationClimateSummaryData;
import com.aqi.dto.meteo.AirQualityResponse;
import com.aqi.dto.meteo.WeatherForecastResponse;
import com.aqi.mapper.OpenMeteoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenMeteoService {

    private final OpenMeteoClient meteoClient;
    private final OpenMeteoMapper meteoMapper;

    public LocationClimateData getLocationClimateData(Double latitude, Double longitude) {
        log.info("Fetching climate data for {}, {}", latitude, longitude);

        CompletableFuture<WeatherForecastResponse> weatherFuture = CompletableFuture.supplyAsync(() ->
                meteoClient.fetchWeather(latitude, longitude)
        );

        CompletableFuture<AirQualityResponse> aqiFuture = CompletableFuture.supplyAsync(() ->
                meteoClient.fetchAirQuality(latitude, longitude)
        );

        CompletableFuture<ReverseGeocodingResponse> geoFuture = CompletableFuture.supplyAsync(() ->
                meteoClient.fetchLocationName(latitude, longitude)
        );

        try {
            CompletableFuture.allOf(weatherFuture, aqiFuture, geoFuture).join();

            return meteoMapper.mapToClimateData(
                    weatherFuture.get(),
                    aqiFuture.get(),
                    geoFuture.get()
            );
        } catch (Exception e) {
            log.error("Error during data aggregation", e);
            throw new RuntimeException("Failed to fetch climate data", e);
        }
    }
    
    public LocationClimateSummaryData getLocationClimateSummaryData(Double latitude, Double longitude) {
        log.info("Fetching climate summary data for {}, {}", latitude, longitude);

        CompletableFuture<WeatherForecastResponse> weatherFuture = CompletableFuture.supplyAsync(() ->
                meteoClient.fetchWeatherSummary(latitude, longitude)
        );

        CompletableFuture<AirQualityResponse> aqiFuture = CompletableFuture.supplyAsync(() ->
                meteoClient.fetchAirQualitySummary(latitude, longitude)
        );

        CompletableFuture<ReverseGeocodingResponse> geoFuture = CompletableFuture.supplyAsync(() ->
                meteoClient.fetchLocationName(latitude, longitude)
        );

        try {
            CompletableFuture.allOf(weatherFuture, aqiFuture, geoFuture).join();

            return meteoMapper.mapToLocationClimateSummaryData(
                    weatherFuture.get(),
                    aqiFuture.get(),
                    geoFuture.get()
            );
        } catch (Exception e) {
            log.error("Error during data aggregation", e);
            throw new RuntimeException("Failed to fetch climate data summary", e);
        }
    }
}
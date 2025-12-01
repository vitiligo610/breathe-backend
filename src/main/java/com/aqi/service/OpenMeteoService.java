package com.aqi.service;

import com.aqi.client.OpenMeteoClient;
import com.aqi.dto.geocoding.ReverseGeocodingResponse;
import com.aqi.dto.location.LocationAirQualityHistoryData;
import com.aqi.dto.location.LocationClimateData;
import com.aqi.dto.location.LocationClimateSummaryData;
import com.aqi.dto.location.MapLocationData;
import com.aqi.dto.meteo.AirQualityResponse;
import com.aqi.dto.meteo.WeatherForecastResponse;
import com.aqi.dto.openaq.ClusterProjection;
import com.aqi.dto.report.PollutionReportDto;
import com.aqi.mapper.OpenMeteoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenMeteoService {

    private final OpenMeteoClient meteoClient;
    private final OpenMeteoMapper meteoMapper;
    private final OpenAqService openAqService;
    private final CommunityReportService reportService;

    public LocationClimateData getLocationClimateData(Double latitude, Double longitude, Double reportsRadiusKm) {
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

        CompletableFuture<List<PollutionReportDto>> nearbyReportsFuture = CompletableFuture.supplyAsync(() ->
                reportService.getReportsNearLocation(latitude, longitude, reportsRadiusKm)
        );

        try {
            CompletableFuture.allOf(weatherFuture, aqiFuture, geoFuture, nearbyReportsFuture).join();

            return meteoMapper.mapToClimateData(
                    weatherFuture.get(),
                    aqiFuture.get(),
                    geoFuture.get(),
                    nearbyReportsFuture.get()
            );
        } catch (Exception e) {
            log.error("Error during data aggregation", e);
            throw new RuntimeException("Failed to fetch climate data", e);
        }
    }
    
    public LocationClimateSummaryData getLocationClimateSummaryData(Double latitude, Double longitude, Double reportsRadiusKm) {
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

        CompletableFuture<List<PollutionReportDto>> nearbyReportsFuture = CompletableFuture.supplyAsync(() ->
                reportService.getReportsNearLocation(latitude, longitude, reportsRadiusKm)
        );

        try {
            CompletableFuture.allOf(weatherFuture, aqiFuture, geoFuture, nearbyReportsFuture).join();

            return meteoMapper.mapToLocationClimateSummaryData(
                    weatherFuture.get(),
                    aqiFuture.get(),
                    geoFuture.get(),
                    nearbyReportsFuture.get()
            );
        } catch (Exception e) {
            log.error("Error during data aggregation", e);
            throw new RuntimeException("Failed to fetch climate data summary", e);
        }
    }

    public LocationAirQualityHistoryData getLocationAQHistoryData(Double latitude, Double longitude) {
        log.info("Fetching air quality history data for {}, {}", latitude, longitude);

        CompletableFuture<AirQualityResponse> aqiFuture = CompletableFuture.supplyAsync(() ->
                meteoClient.fetchAirQualityHistory(latitude, longitude)
        );

        try {
            CompletableFuture.allOf(aqiFuture).join();

            return meteoMapper.mapToLocationAQHistoryData(
                    aqiFuture.get()
            );
        } catch (Exception e) {
            log.error("Error during data aggregation", e);
            throw new RuntimeException("Failed to fetch air quality history data", e);
        }
    }

    public List<MapLocationData> getMapLocations(List<Double> bbox, Integer gridResolution) {
        List<ClusterProjection> clusters =
                openAqService.getClustersInBoundingBox(bbox, gridResolution);



        if (clusters.isEmpty()) {
            return Collections.emptyList();
        }

        var responseFuture = fetchAqiForClusters(clusters);

        var reportsFuture = CompletableFuture.supplyAsync(() ->
                reportService.getReportsInBoundingBox(bbox)
        );

        try {
            CompletableFuture.allOf(responseFuture, reportsFuture).join();

            return meteoMapper.mapToMapLocations(
                    responseFuture.get(), clusters,
                    reportsFuture.get()
            );
        } catch (Exception e) {
            log.error("Error during data aggregation", e);
            throw new RuntimeException("Failed to fetch map data", e);
        }
    }

    private CompletableFuture<AirQualityResponse[]> fetchAqiForClusters(List<ClusterProjection> clusters) {
        List<Double> latPoints = new ArrayList<>();
        List<Double> lonPoints = new ArrayList<>();

        for (var cluster : clusters) {
            latPoints.add(cluster.getLat());
            lonPoints.add(cluster.getLon());
        }

        log.info("Batch fetching AQI for {} clusters, latitudes: {}, longitudes {}", clusters.size(), latPoints, lonPoints);
        return CompletableFuture.supplyAsync(() ->
                meteoClient.fetchAirQualityBatch(latPoints, lonPoints)
        );
    }
}
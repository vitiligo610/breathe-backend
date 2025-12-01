package com.aqi.controller;

import com.aqi.dto.location.LocationAirQualityHistoryData;
import com.aqi.dto.location.LocationClimateData;
import com.aqi.dto.location.LocationClimateSummaryData;
import com.aqi.dto.location.MapLocationData;
import com.aqi.dto.report.PollutionReportDto;
import com.aqi.request.CreateReportRequest;
import com.aqi.service.CommunityReportService;
import com.aqi.service.OpenMeteoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location")
public class LocationDataController {

    private final OpenMeteoService openMeteoService;
    private final CommunityReportService communityReportService;

    @GetMapping
    public LocationClimateData getLocationClimateData(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(name = "reports_radius_km", defaultValue = "10.0") Double reportsRadiusKm
    ) {
        return openMeteoService.getLocationClimateData(latitude, longitude, reportsRadiusKm);
    }

    @GetMapping("/summary")
    public LocationClimateSummaryData getLocationClimateSummaryData(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(name = "reports_radius_km", defaultValue = "10.0") Double reportsRadiusKm
    ) {
        return openMeteoService.getLocationClimateSummaryData(latitude, longitude, reportsRadiusKm);
    }

    @GetMapping("/history")
    public LocationAirQualityHistoryData getLocationAQHistoryData(
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        return openMeteoService.getLocationAQHistoryData(latitude, longitude);
    }

    @GetMapping("/map")
    public List<MapLocationData> getMapData(
            @RequestParam("bounding_box") List<Double> boundingBox,
            @RequestParam(name = "grid_resolution", defaultValue = "10") Integer gridResolution
    ) {
        return openMeteoService.getMapLocations(boundingBox, gridResolution);
    }

    @PostMapping("/report")
    public PollutionReportDto createReport(
            @RequestBody CreateReportRequest request
    ) {
        return communityReportService.createReport(request);
    }

    @GetMapping("/reports")
    public List<PollutionReportDto> getReports(
            @RequestParam("bounding_box") List<Double> boundingBox
    ) {
        return communityReportService.getReportsInBoundingBox(boundingBox);
    }
}

package com.aqi.controller;

import com.aqi.dto.location.LocationClimateData;
import com.aqi.service.OpenMeteoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location")
public class LocationDataController {

    private final OpenMeteoService openMeteoService;

    @GetMapping
    public LocationClimateData getLocationClimateData(@RequestParam Double latitude, @RequestParam Double longitude) {
        return openMeteoService.getLocationClimateData(latitude, longitude);
    }
}

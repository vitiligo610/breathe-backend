package com.aqi.controller;

import com.aqi.dto.location.LocationDataDto;
import com.aqi.service.OpenWeatherMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aqi")
public class AqiController {
    private final OpenWeatherMapService openWeatherMapService;

    @GetMapping
    public LocationDataDto getAqi(@RequestParam Double lat, @RequestParam Double lon) {
        return openWeatherMapService.getExternalLocationData(lat, lon);
    }
}
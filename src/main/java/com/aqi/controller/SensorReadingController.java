package com.aqi.controller;

import com.aqi.dto.sensor.SensorDataDto;
import com.aqi.service.SensorReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorReadingController {
    private final SensorReadingService sensorReadingService;

    /**
     * API endpoint to get a paginated list of sensor readings.
     * Supports time-range filtering via query parameters.
     * <p>
     * Example: GET /api/sensors/123e4567-e89b-12d3-a456-426614174000/readings
     * Example: GET /api/sensors/.../readings?page=0&size=10
     * Example: GET /api/sensors/.../readings?start=2025-11-16T00:00:00Z&end=2025-11-17T00:00:00Z
     */
    @GetMapping("/{id}/readings")
    public Page<SensorDataDto> getSensorReadings(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return sensorReadingService.getSensorReadings(id, start, end, pageable);
    }
}
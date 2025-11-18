package com.aqi.controller;

import com.aqi.entity.SensorNode;
import com.aqi.service.SensorNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nodes")
@RequiredArgsConstructor
public class SensorNodeController {
    private final SensorNodeService sensorNodeService;

    /**
     * Get a paginated and filtered list of sensor nodes.
     * <p>
     * Example: GET /api/nodes
     * Example: GET /api/nodes?active=true&name=lab&page=0&size=5
     * Example: GET /api/nodes?location=floor
     */
    @GetMapping
    public Page<SensorNode> getNodes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return sensorNodeService.getNodes(name, location, active, pageable);
    }
}
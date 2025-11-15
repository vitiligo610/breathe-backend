package com.aqi.controller;

import com.aqi.dto.tenant.TenantDto;
import com.aqi.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/")
    ResponseEntity<TenantDto> getCurrentTenant() {
        TenantDto tenantDto = tenantService.getCurrentTenant();
        return ResponseEntity.ok(tenantDto);
    }
}

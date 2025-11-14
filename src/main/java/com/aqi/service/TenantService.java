package com.aqi.service;

import com.aqi.dto.tenant.TenantDto;
import com.aqi.entity.Tenant;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.TenantRepository;
import com.aqi.security.TenantAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenantService {
    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantDto getCurrentTenant() {
        TenantAuthentication authentication = (TenantAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = authentication.getTenantId();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found!"));

        return new TenantDto(tenant.getId(), tenant.getName());
    }

    public String createNewTenant(String tenantName) {
        String secretToken = "sk_main_" + UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");

        Tenant tenant = new Tenant();
        tenant.setName(tenantName);
        tenant.setSecretToken(secretToken);

        tenantRepository.save(tenant);

        logger.info("Tenant '{}' created successfully with ID: {}", tenantName, tenant.getId());

        return secretToken;
    }
}

package com.aqi.service;

import com.aqi.dto.tenant.TenantDto;
import com.aqi.entity.Tenant;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.TenantRepository;
import com.aqi.security.TenantAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {
    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public TenantDto getCurrentTenant() {
        TenantAuthentication authentication = (TenantAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = authentication.getTenantId();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found!"));

        return new TenantDto(tenant.getId(), tenant.getName());
    }

    @Transactional
    public String createNewTenant(String tenantName) {
        String secretToken = "sk_main_" + UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");

        Tenant tenant = new Tenant();
        tenant.setName(tenantName);
        tenant.setSecretToken(secretToken);

        tenantRepository.save(tenant);

        log.info("Tenant '{}' created successfully with ID: {}", tenantName, tenant.getId());

        return secretToken;
    }
}

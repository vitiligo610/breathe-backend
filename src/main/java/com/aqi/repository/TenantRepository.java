package com.aqi.repository;

import com.aqi.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findById(long id);
    Optional<Tenant> findBySecretToken(String secretToken);
}

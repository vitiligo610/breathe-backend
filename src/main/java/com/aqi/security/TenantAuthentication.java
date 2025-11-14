package com.aqi.security;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class TenantAuthentication implements Authentication {
    private final String tenantSecret;
    private final String tenantName;
    private boolean authenticated = true;

    @Getter
    private final Long tenantId;

    public TenantAuthentication(Long tenantId, String tenantName, String tenantSecret) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantSecret = tenantSecret;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return tenantSecret;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return tenantName;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return tenantName;
    }
}

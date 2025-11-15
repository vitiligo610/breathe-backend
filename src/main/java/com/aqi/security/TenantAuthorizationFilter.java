package com.aqi.security;

import com.aqi.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantAuthorizationFilter extends OncePerRequestFilter {
    private final TenantRepository tenantRepository;

    @Value("${app.auth.tenant-header:X-Tenant-Secret}")
    private String tenantHeaderName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String secretToken = request.getHeader(tenantHeaderName);

        if (secretToken != null && !secretToken.isEmpty()) {
            tenantRepository.findBySecretToken(secretToken).ifPresentOrElse(tenant -> {
                TenantAuthentication authentication = new TenantAuthentication(
                        tenant.getId(),
                        tenant.getName(),
                        secretToken
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }, () -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                try {
                    response.getWriter().write("Invalid tenant secret token.");
                } catch (IOException e) {
                    log.error("Could not write response", e);
                }
            });
        }

        if (!response.isCommitted()) {
            filterChain.doFilter(request, response);
        }
    }
}

package com.moptra.invoice.tenants.filter;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.tenants.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // log for debug purpose only
            log.info("TENANT FILTER: URI = {}", request.getRequestURI());
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                String tenantId = userDetails.getTenantId();
                if (tenantId != null && !tenantId.isBlank()) {
                    TenantContext.setTenantId(tenantId);
                    log.info("TENANT FILTER: Tenant = {}", tenantId);
                } else {
                    log.info("TENANT FILTER: No tenant ID found");
                }
            } else {
                log.info("TENANT FILTER: No CustomUserDetails principal");
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }
}

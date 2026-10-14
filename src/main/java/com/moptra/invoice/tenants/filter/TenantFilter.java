package com.moptra.invoice.tenants.filter;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.tenants.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If authentication is already present (or if you extract tenant from a custom header/JWT)
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                if (userDetails.getTenantId() != null) {
                    TenantContext.setTenantId(userDetails.getTenantId());
                    System.out.println("FILTER: Set Tenant Context to -> " + userDetails.getTenantId());
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // ALWAYS clear it to prevent bleeding into other requests on thread reuse
            TenantContext.clear();
        }
    }
}
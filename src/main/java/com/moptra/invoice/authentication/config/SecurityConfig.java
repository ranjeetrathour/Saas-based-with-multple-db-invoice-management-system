package com.moptra.invoice.authentication.config;

import com.moptra.invoice.authentication.filter.JwtAuthenticationFilter;
import com.moptra.invoice.tenants.filter.TenantFilter;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures HTTP security, stateless JWT + tenant routing filter chain,
 * password encoding, and authentication manager for the multi-tenant platform.
 *
 * FLOW CONTEXT:
 * - Public onboarding & auth endpoints (/api/v1/auth/login, /api/v1/onboarding/**)
 *   are permitted before login; JwtAuthenticationFilter sets principal when a valid
 *   token is present, and TenantFilter resolves Master vs. Tenant DB context.
 * - Authenticated flows cover vendor internal staff, restricted client portal users
 *   (e.g., Petco), client-created projects/per-employee SOWs, timesheets, and billing.
 *
 * FUTURE CHANGE POINT:
 * - Enable method security (@EnableMethodSecurity) and configure CORS rules when
 *   frontend origins are finalized.
 * - Restrict /api/v1/onboarding/** (except company registration) once admin auth is enforced.
 */
@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final TenantFilter tenantFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(@Nonnull HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/v1/auth/login",
                                "/api/v1/onboarding/**",
                                "/v1/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v1/swagger-ui/**",
                                "/v1/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tenantFilter, JwtAuthenticationFilter.class)
                .build();
    }

    /**
     * Prevents Spring Boot from auto-registering TenantFilter as a standalone servlet filter
     * so it only runs in the exact order configured inside SecurityFilterChain.
     */
    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilterRegistration() {
        FilterRegistrationBean<TenantFilter> registration = new FilterRegistrationBean<>(tenantFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(@Nonnull AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
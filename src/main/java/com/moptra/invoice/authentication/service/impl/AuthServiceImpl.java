
package com.moptra.invoice.authentication.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.authentication.service.AuthService;
import com.moptra.invoice.authentication.service.JwtService;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.user.dto.req.LoginRequest;
import com.moptra.invoice.user.dto.res.LoginResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(@NonNull LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Email is required");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password");
        } catch (AuthenticationException ex) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");
        }

        if (!authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {

            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Invalid authentication details");
        }

        String token = jwtService.generateToken(userDetails);

        log.info("Authenticated user='{}' tenantId='{}' companyId='{}'",userDetails.getUsername(),userDetails.getTenantId(),userDetails.getCompanyId());

        return LoginResponse.builder()
                .accessToken(token)
                .email(userDetails.getUsername())
                .tenantId(userDetails.getTenantId())
                .message("Login successful")
                .build();
    }
}

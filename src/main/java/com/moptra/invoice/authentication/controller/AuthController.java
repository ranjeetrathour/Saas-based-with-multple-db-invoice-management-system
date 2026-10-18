package com.moptra.invoice.authentication.controller;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.authentication.service.JwtService;
import com.moptra.invoice.user.dto.req.LoginRequest;
import com.moptra.invoice.user.dto.res.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * need service class
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1. Authenticate against Master DB (UserIdentity)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Retrieve CustomUserDetails containing tenant context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 3. Issue JWT containing tenantId
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(
                LoginResponse.builder()
                        .accessToken(token)
                        .email(userDetails.getUsername())
                        .tenantId(userDetails.getTenantId())
                        .message("Login successful")
                        .build()
        );
    }
}
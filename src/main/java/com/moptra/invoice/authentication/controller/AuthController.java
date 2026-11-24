package com.moptra.invoice.authentication.controller;

import com.moptra.invoice.authentication.service.AuthService;
import com.moptra.invoice.user.dto.req.LoginRequest;
import com.moptra.invoice.user.dto.res.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints for SaaS Platform, Vendor Tenant, and Restricted Client Portal logins.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authenticates identities and issues scoped JWT tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates credentials against Master DB identity and returns a scoped JWT access token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Account inactive or suspended", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
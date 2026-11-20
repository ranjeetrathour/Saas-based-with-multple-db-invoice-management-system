package com.moptra.invoice.client.controller;

import com.moptra.invoice.client.dto.req.ClientCompanyRequest;
import com.moptra.invoice.client.dto.res.ClientCompanyResponse;
import com.moptra.invoice.client.service.ClientCompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for onboarding Client Companies (e.g., Petco) and optional
 * client branches inside the active Vendor/Tenant database (e.g., tenant_shyftlab).
 *
 * FLOW CONTEXT:
 * - Main vendor (e.g., Shyftlab) is already provisioned with its own Tenant DB.
 * - Authorized vendor user onboards client company here; later restricted client portal
 *   users are provisioned and client creates projects / per-employee SOWs / allocations.
 *
 * FUTURE CHANGE POINT:
 * - Enforce @PreAuthorize role/permission check (e.g., COMPANY_ADMIN or CLIENT_ONBOARD)
 *   and block restricted CLIENT portal users once RBAC permissions are wired.
 */
@RestController
@RequestMapping("/api/v1/client-companies")
@AllArgsConstructor
@Tag(
        name = "Client Company Onboarding",
        description = "Onboards client companies and optional branches into the active tenant database"
)
public class ClientCompanyOnboardingController {

    private final ClientCompanyService clientCompanyService;

    /**
     * Creates a new client company (and optional branches) in the currently resolved tenant DB.
     *
     * FUTURE CHANGE POINT: Optionally trigger restricted client portal user provisioning
     * here or in a dedicated client-user onboarding endpoint.
     */
    @PostMapping
    @Operation(
            summary = "Create client company",
            description = "Onboards a client company inside the current tenant database with optional branches"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Client company created successfully",
                    content = @Content(schema = @Schema(implementation = ClientCompanyResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed or tenant context missing", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized to onboard client companies", content = @Content),
            @ApiResponse(responseCode = "409", description = "Client company code already exists", content = @Content)
    })
    public ResponseEntity<ClientCompanyResponse> createClientCompany(
            @Valid @RequestBody ClientCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientCompanyService.createClientCompany(request));
    }
}
package com.moptra.invoice.client.controller;

import com.moptra.invoice.client.service.ClientUserService;
import com.moptra.invoice.user.dto.req.CreateClientUserRequest;
import com.moptra.invoice.user.dto.res.ClientUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for provisioning restricted Client Portal users (e.g., Petco users)
 * scoped to a specific ClientCompany inside the active tenant.
 *
 * FUTURE CHANGE POINT:
 * - Restrict via @PreAuthorize so only vendor COMPANY_ADMIN or the client's own
 *   CLIENT_ADMIN can create users for that clientCompanyId.
 */
@RestController
@RequestMapping("/api/v1/client-companies/{clientCompanyId}/users")
@RequiredArgsConstructor
@Tag(name = "Client Portal Users", description = "Provisions restricted login users for a client company")
public class ClientUserController {

    private final ClientUserService clientUserService;

    @PostMapping
    @Operation(
            summary = "Create restricted client portal user",
            description = "Creates a CLIENT login identity in Master DB and a lightweight User (without Employee) in Tenant DB"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Client portal user created successfully",
                    content = @Content(schema = @Schema(implementation = ClientUserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid client role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client company not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "User email already exists", content = @Content)
    })
    public ResponseEntity<ClientUserResponse> createClientUser(@Parameter(description = "Client company ID in Tenant DB", required = true) @PathVariable String clientCompanyId, @Valid @RequestBody CreateClientUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientUserService.createClientUser(clientCompanyId, request));
    }
}
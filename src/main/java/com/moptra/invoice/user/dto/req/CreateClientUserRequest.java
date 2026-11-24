package com.moptra.invoice.user.dto.req;

import com.moptra.invoice.roles.entites.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request record for creating a restricted Client Portal user (e.g., Petco admin/TL/finance).
 * Does NOT require an Employee record; uses UserIdentity (Master DB) + lightweight User (Tenant DB).
 */
@Schema(description = "Request payload for creating a restricted client portal user")
public record CreateClientUserRequest(
        @NotBlank(message = "Name is required")
        @Schema(description = "Full name of the client portal user", example = "Sarah Connor")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "Login email for the client user", example = "sarah.connor@petco.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Schema(description = "Initial password for client portal login", example = "PetcoPass@123")
        String password,

        @NotNull(message = "Role is required")
        @Schema(description = "Restricted client role (CLIENT_ADMIN, CLIENT_TL, CLIENT_FINANCE)", example = "CLIENT_ADMIN")
        UserRole role
) {}
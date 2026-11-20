package com.moptra.invoice.tenants.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request record to register a Main Tenant / Vendor company (e.g., Shyftlab),
 * its internal branches, and its initial COMPANY_ADMIN user.
 *
 * FUTURE CHANGE POINT: Add optional initial HR/Finance user provisioning or
 * BYO-DB connection details if exposed during onboarding.
 */
@Schema(description = "Payload to register a main tenant (vendor) company, branches, and initial admin")
public record RegisterCompanyRequest(
        @NotBlank(message = "Company name is required")
        @Schema(description = "Legal/display name of the vendor company", example = "Shyftlab")
        String companyName,

        @NotBlank(message = "Company code is required")
        @Schema(description = "Unique short code for the vendor company", example = "SHYFTLAB")
        String companyCode,

        @NotBlank(message = "Company email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "Official contact email of the vendor company", example = "contact@shyftlab.com")
        String companyEmail,

        @Schema(description = "Vendor company contact phone", example = "+91-9876543210")
        String companyPhone,

        @Schema(description = "Vendor website URL", example = "https://shyftlab.com")
        String website,

        @NotEmpty(message = "At least one branch is required")
        @Valid
        @Schema(description = "List of internal vendor branches with GST and billing address details")
        List<BranchRequest> branches,

        @NotBlank(message = "Admin name is required")
        @Schema(description = "Full name of the initial vendor admin", example = "Vikram Mehta")
        String adminName,

        @NotBlank(message = "Admin email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "Login email for the initial vendor admin", example = "admin@shyftlab.com")
        String adminEmail,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Schema(description = "Initial password (minimum 8 characters)", example = "StrongPass@123")
        String password
) {}
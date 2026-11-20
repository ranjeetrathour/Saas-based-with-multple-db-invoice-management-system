package com.moptra.invoice.tenants.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response record returned after registering a Main Tenant / Vendor company and its initial admin.
 * FUTURE CHANGE POINT: Remove plaintext password in production and return onboarding status/invite info.
 */
@Schema(description = "Response payload after main tenant (vendor) registration")
@Builder
public record RegisterCompanyResponse(
        @Schema(description = "Registered vendor company ID", example = "cmp_001")
        String companyId,

        @Schema(description = "Created initial admin user ID", example = "1001")
        String userId,

        @Schema(description = "Registered vendor company name", example = "Shyftlab")
        String companyName,

        @Schema(description = "Initial admin login email", example = "admin@shyftlab.com")
        String adminEmail,

        @Schema(description = "Avoid returning raw password in production APIs", hidden = true)
        String password,

        @Schema(description = "Provisioning / company status", example = "ACTIVE")
        String status,

        @Schema(description = "Human-readable onboarding status message", example = "Company and admin user created successfully")
        String message
) {}
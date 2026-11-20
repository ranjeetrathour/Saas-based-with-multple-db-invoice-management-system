package com.moptra.invoice.tenants.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response record returning complete Main Tenant / Vendor company profile (e.g., Shyftlab)
 * along with its internal branches[cite: 1].
 *
 * FUTURE CHANGE POINT: Include tenant database status or summary counts (internal users,
 * onboarded clients) if needed by admin views.
 */
@Schema(description = "Detailed view of a registered vendor/tenant company")
public record TenantDetailsResponse(
        @Schema(description = "Vendor company ID", example = "cmp_001")
        String companyId,

        @Schema(description = "Vendor company name", example = "Shyftlab")
        String companyName,

        @Schema(description = "Unique vendor company code", example = "SHYFTLAB")
        String companyCode,

        @Schema(description = "Primary contact email of the vendor company", example = "admin@shyftlab.com")
        String companyEmail,

        @Schema(description = "Contact phone number of the vendor company", example = "+91-9876543210")
        String companyPhone,

        @Schema(description = "Official website URL", example = "https://shyftlab.com")
        String website,

        @Schema(description = "Lifecycle status of the vendor company", example = "ACTIVE")
        String status,

        @Schema(description = "Internal branches belonging to this vendor company")
        List<BranchResponse> branches
) {}
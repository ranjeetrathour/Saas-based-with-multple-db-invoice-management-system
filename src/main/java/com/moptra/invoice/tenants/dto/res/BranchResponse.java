package com.moptra.invoice.tenants.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response record representing a saved internal branch of the Main Tenant / Vendor company
 * (e.g., Shyftlab office branch)[cite: 1].
 *
 * FUTURE CHANGE POINT: Add gstNo, branchName, or isActive if needed in branch detail/list views.
 */
@Schema(description = "Vendor branch response payload")
public record BranchResponse(
        @Schema(description = "Branch identifier", example = "br_101")
        String branchId,

        @Schema(description = "Unique branch code", example = "SHYFT-NOIDA")
        String branchCode,

        @Schema(description = "Billing address of the branch", example = "Sector 62, Noida, UP")
        String branchAddress,

        @Schema(description = "City where the branch is located", example = "Noida")
        String branchCity,

        @Schema(description = "Country where the branch is located", example = "India")
        String branchCountry
) {}
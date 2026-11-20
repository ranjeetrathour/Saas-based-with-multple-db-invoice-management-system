package com.moptra.invoice.tenants.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request record to create an internal branch for the Main Tenant / Vendor company.
 * FUTURE CHANGE POINT: Parameterize GSTIN validation if onboarding non-GST / international tenants.
 */
@Schema(description = "Branch details including GSTIN and billing address for a vendor company")
public record BranchRequest(
        @NotBlank(message = "Branch name is required")
        @Schema(description = "Name of the vendor branch", example = "Shyftlab Noida")
        String branchName,

        @NotBlank(message = "Branch code is required")
        @Schema(description = "Unique code for the vendor branch", example = "SHYFT-NOIDA")
        String branchCode,

        @NotBlank(message = "GST Number is required")
        @Pattern(
                regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GSTIN format"
        )
        @Schema(description = "15-character GSTIN of the branch", example = "09ABCDE1234F1Z5")
        String gstNo,

        @NotBlank(message = "Branch billing address is required")
        @Schema(description = "Billing address associated with this branch", example = "Sector 62, Noida, UP")
        String branchAddress,

        @Schema(description = "City name", example = "Noida")
        String branchCity,

        @Schema(description = "Country name", example = "India")
        String branchCountry
) {}
package com.moptra.invoice.client.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request record representing an optional branch of a Client Company (e.g., Petco).
 * Can be passed inline during ClientCompany creation or used later if branch-add/update
 * endpoints are exposed for authorized users.
 *
 * FUTURE CHANGE POINT: Add branchContactEmail or taxRegionCode if invoices/SOWs
 * are routed to specific client branches.
 */
@Schema(description = "Optional branch details for a client company")
public record ClientBranchRequest(
        @NotBlank(message = "Branch name is required")
        @Schema(description = "Name of the client branch", example = "Petco West Hub")
        String branchName,

        @NotBlank(message = "Branch code is required")
        @Schema(description = "Unique branch code within the client company", example = "PETCO-WEST")
        String branchCode,

        @Schema(description = "Branch-specific GST or tax number", example = "27AABCP1234C1Z2")
        String gst,

        @Schema(description = "Billing address for this client branch", example = "100 Harbor Dr, San Diego")
        String billingAddress,

        @Schema(description = "City name", example = "San Diego")
        String city,

        @Schema(description = "State name", example = "California")
        String state,

        @Schema(description = "Country name", example = "USA")
        String country,

        @Schema(description = "Whether this branch is active", example = "true")
        Boolean isActive
) {}
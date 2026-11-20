package com.moptra.invoice.client.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response record representing a saved branch of a Client Company (e.g., Petco).
 * FUTURE CHANGE POINT: Add branchId if frontend performs direct branch edits/deletes.
 */
@Schema(description = "Client branch response payload")
public record ClientBranchResponse(
        @Schema(description = "Parent client company ID", example = "cli_201")
        String clientCompanyId,

        @Schema(description = "Client branch name", example = "Petco West Hub")
        String branchName,

        @Schema(description = "Client branch code", example = "PETCO-WEST")
        String branchCode,

        @Schema(description = "Branch GST / tax number", example = "27AABCP1234C1Z2")
        String gst,

        @Schema(description = "Branch billing address", example = "100 Harbor Dr, San Diego")
        String billingAddress,

        @Schema(description = "City", example = "San Diego")
        String city,

        @Schema(description = "State", example = "California")
        String state,

        @Schema(description = "Country", example = "USA")
        String country,

        @Schema(description = "Active status of the client branch", example = "true")
        Boolean isActive
) {}
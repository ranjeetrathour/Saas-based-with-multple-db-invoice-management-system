package com.moptra.invoice.client.dto.req;

import jakarta.validation.constraints.NotBlank;

public record ClientBranchRequest(
        @NotBlank(message = "Branch name is required")
        String branchName,

        @NotBlank(message = "Branch code is required")
        String branchCode,

        String gst,
        String billingAddress,
        String city,
        String state,
        String country,
        Boolean isActive
) {}
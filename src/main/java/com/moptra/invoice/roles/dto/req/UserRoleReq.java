package com.moptra.invoice.roles.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRoleReq(
        @NotBlank(message = "Role name is required")
        @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
        @Schema(description = "Unique role name within the tenant", example = "FINANCE_APPROVER")
        String name,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Schema(description = "Role responsibility description", example = "Calculates and reviews weekly client billing")
        String description
) {}
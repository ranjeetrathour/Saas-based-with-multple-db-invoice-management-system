package com.moptra.invoice.roles.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Response payload for a tenant custom role")
public record UserRoleRes(
        @Schema(description = "Role ID in Tenant DB", example = "role_101")
        String id,

        @Schema(description = "Role name", example = "FINANCE_APPROVER")
        String name,

        @Schema(description = "Role description", example = "Calculates and reviews weekly client billing")
        String description
) {}
package com.moptra.invoice.user.dto.res;

import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.user.enums.IdentityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response payload for a restricted client portal user")
public record ClientUserResponse(
        @Schema(description = "Master DB UserIdentity ID", example = "uid_901")
        String identityId,

        @Schema(description = "Tenant DB User ID", example = "usr_401")
        String tenantUserId,

        @Schema(description = "Vendor Company ID", example = "cmp_001")
        String companyId,

        @Schema(description = "Client Company ID in Tenant DB", example = "cli_201")
        String clientCompanyId,

        @Schema(description = "Full name", example = "Sarah Connor")
        String name,

        @Schema(description = "Login email", example = "sarah.connor@petco.com")
        String email,

        @Schema(description = "Identity scope", example = "CLIENT")
        IdentityType identityType,

        @Schema(description = "Assigned client role", example = "CLIENT_ADMIN")
        UserRole role,

        @Schema(description = "Account status", example = "ACTIVE")
        AccountStatus status,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt
) {}
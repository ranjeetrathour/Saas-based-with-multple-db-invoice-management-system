package com.moptra.invoice.user.entities.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
//import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.user.enums.IdentityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Master DB authentication identity for Platform (SaaS), Vendor (TENANT),
 * and Restricted Client Portal (CLIENT) users.
 *
 * DESIGN NOTES:
 * - Stored in Master DB for login routing, tenant context resolution, and client scope restriction.
 * - clientCompanyId is stored as a plain ID (not a cross-DB @ManyToOne entity) because
 *   ClientCompany lives in the Tenant DB.
 * - Vendor/Tenant user: identityType = TENANT, company != null, clientCompanyId == null.
 * - Client portal user: identityType = CLIENT, company != null, clientCompanyId != null.
 * - Platform user: identityType = PLATFORM, company == null, clientCompanyId == null.
 *
 * FUTURE CHANGE POINT:
 * - Map fine-grained custom tenant roles/permissions from Tenant DB (roles table) after login/tenant switching,
 *   and add lastLoginAt or mfaEnabled if needed.
 */
@Entity
@Table(
        name = "user_identities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_identity_company_client_email",
                        columnNames = {"company_id", "client_company_id", "email"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication identity for platform, vendor tenant, and restricted client portal users")
public class UserIdentity extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @Schema(description = "Vendor/tenant company context (null for PLATFORM identities)")
    private Company company;

    @Column(name = "client_company_id")
    @Schema(description = "Tenant DB client_companies.id for CLIENT portal users; null for PLATFORM/TENANT users")
    private String clientCompanyId;

    @Column(nullable = false)
    @Schema(description = "Full name of the user", example = "Rahul Sharma")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Login email address", example = "rahul@petco.com")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", nullable = false)
    @Schema(description = "Identity scope: PLATFORM, TENANT, or CLIENT", example = "CLIENT")
    private IdentityType identityType;

    @Column(name = "password_hash")
    @Schema(description = "Hashed user password (never expose in API responses)")
    private String passwordHash;

    // FUTURE CHANGE POINT: Keep base/platform-level role here for initial auth/routing,
    // while custom tenant roles are managed in the Tenant DB roles table.
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Schema(description = "Role governing initial/scope access", example = "CLIENT_ADMIN")
    private String role; // this is taken as string because role is present in the client db

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Account status", example = "ACTIVE")
    private AccountStatus status;

    /**
     * Returns true when this identity belongs to a restricted client portal user.
     */
    public boolean isClientPortalUser() {
        return this.identityType == IdentityType.CLIENT;
    }

    /**
     * Returns true when this identity belongs to a SaaS platform user.
     */
    public boolean isPlatformUser() {
        return this.identityType == IdentityType.PLATFORM;
    }
}
package com.moptra.invoice.user.entities.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.client.entities.ClientCompany;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.user.enums.IdentityType;
import com.moptra.invoice.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Stores login identities for Platform/SaaS users, Vendor (Tenant) employees/admins,
 * and Restricted Client Portal users (e.g., Petco admin/TL/finance).
 *
 * ISOLATION RULE:
 * - Vendor/Tenant user: company != null, clientCompany == null
 * - Client portal user: company != null, clientCompany != null
 *
 * FUTURE CHANGE POINT: Add lastLoginAt, mfaEnabled, or branchId if branch-scoped access is required.
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
@Schema(description = "Authentication and authorization identity for vendor and client portal users")
public class UserIdentity extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @Schema(description = "Main vendor/tenant company context (null only for platform-level SAAS_ADMIN)")
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_company_id")
    @Schema(description = "Populated for restricted client users (e.g., Petco); null for vendor internal users")
    private ClientCompany clientCompany;

    @Column(nullable = false)
    @Schema(description = "Full name of the user", example = "Rahul Sharma")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Login email address (unique within vendor + client scope)", example = "rahul@petco-client.com")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type")
    @Schema(description = "High-level identity scope: PLATFORM, TENANT, or CLIENT", example = "CLIENT")
    private IdentityType identityType;

    @Column(name = "password_hash")
    @Schema(description = "Hashed user password (never expose in API responses)")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Role governing access permissions", example = "CLIENT_ADMIN")
    private UserRole role; // need to change this field name as platform role

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Account status", example = "ACTIVE")
    private AccountStatus status;

    /**
     * Helper to identify whether this login belongs to a restricted client company user.
     */
    public boolean isClientPortalUser() {
        return this.clientCompany != null;
    }
}
package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.tenants.enums.TenantDatabaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Stores database/connection metadata for a Main Tenant (e.g., Shyftlab).
 * FUTURE CHANGE POINT: Encrypt password/credentials at rest and add schemaName or
 * connectionPoolConfig if per-schema or custom external DB routing is expanded.
 */
@Entity
@Table(name = "tenant_databases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Database routing and connection configuration for a tenant")
public class TenantDatabase extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    @Schema(description = "Associated vendor/tenant company")
    private Company company;

    @Column(name = "database_name", nullable = false, unique = true)
    @Schema(description = "Target database or schema name", example = "shyftlab_db")
    private String databaseName;

    @Column(nullable = false)
    @Schema(description = "Database host address", example = "localhost")
    private String host;

    @Column(nullable = false)
    @Schema(description = "Database port", example = "5432")
    private Integer port;

    @Column(nullable = false)
    @Schema(description = "Database username", example = "tenant_user")
    private String username;

    @Column(nullable = false)
    @Schema(description = "Database password (store encrypted in production)")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Connectivity/provisioning status of the tenant database", example = "ACTIVE")
    private TenantDatabaseStatus status;
}
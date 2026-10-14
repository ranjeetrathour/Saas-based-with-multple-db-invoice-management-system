package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.tenants.enums.TenantDatabaseStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenant_databases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDatabase extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "database_name", nullable = false, unique = true)
    private String databaseName;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantDatabaseStatus status;
}
package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.tenants.enums.ProjectBillingType;
import com.moptra.invoice.tenants.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "project_code", nullable = false, unique = true)
    private String projectCode; // e.g., "HCL-BNK-001"

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Many projects belong to one Client Company (Company B)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_company_id", nullable = false)
    private ClientCompany clientCompany; //

    // Branch of the client or provider executing the project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private ClientBranch branch; // optional

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false)
    private ProjectBillingType billingType; // TIME_AND_MATERIAL, FIXED_COST, NON_BILLABLE

    @Column(name = "total_budget", precision = 12, scale = 2)
    private BigDecimal totalBudget; // Overall project contract amount or limit

    @Column(name = "currency", length = 10, nullable = false)
    private String currency; // USD, INR, EUR

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;
}
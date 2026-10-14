package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity for Client Company Branches (Optional, e.g., HCL Noida / HCL Bangalore)
 */
@Entity
@Table(name = "client_branches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientBranch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_company_id", nullable = false)
    private ClientCompany clientCompany;

    @Column(name = "branch_name", nullable = false)
    private String branchName; // e.g., Noida SEZ Branch

    @Column(name = "branch_code", nullable = false)
    private String branchCode;

    @Column(name = "gst_no")
    private String gst; // Branch-specific GST Number (State wise)

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress; // Branch-specific Billing Address

    private String city;

    private String state;

    private String country;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
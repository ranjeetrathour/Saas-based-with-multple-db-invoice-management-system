package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "branch_code", nullable = false, unique = true)
    private String branchCode;

    // gst number is used for billing address
    @Column(name = "gst_no", nullable = false)
    private String gstNo;

    @Column(name = "branch_address", columnDefinition = "TEXT", nullable = false)
    private String branchAddress;

    @Column(name = "branch_city")
    private String branchCity;

    @Column(name = "branch_country")
    private String branchCountry;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
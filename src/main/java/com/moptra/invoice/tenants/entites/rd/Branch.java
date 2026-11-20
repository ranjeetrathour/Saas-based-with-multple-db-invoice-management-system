package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents internal office branches belonging to the Main Tenant / Vendor (e.g., Shyftlab offices).
 * FUTURE CHANGE POINT: Add stateCode or billingEntityFlag if invoices are issued from specific vendor branches.
 */
@Entity
@Table(name = "branches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Internal branch/office belonging to the vendor (tenant) company")
public class Branch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    @Schema(description = "Parent vendor/tenant company")
    private Company company;

    @Column(name = "branch_name", nullable = false)
    @Schema(description = "Name of the vendor branch", example = "Shyftlab Bengaluru")
    private String branchName;

    @Column(name = "branch_code", nullable = false, unique = true)
    @Schema(description = "Unique code for the vendor branch", example = "SHYFT-BLR")
    private String branchCode;

    // GST number used for tax/billing details
    @Column(name = "gst_no", nullable = false)
    @Schema(description = "GST registration number of the branch", example = "29ABCDE1234F1Z5")
    private String gstNo;

    @Column(name = "branch_address", columnDefinition = "TEXT", nullable = false)
    @Schema(description = "Physical address of the branch")
    private String branchAddress;

    @Column(name = "branch_city")
    @Schema(description = "City where the branch is located", example = "Bengaluru")
    private String branchCity;

    @Column(name = "branch_country")
    @Schema(description = "Country where the branch is located", example = "India")
    private String branchCountry;

    @Column(name = "is_active", nullable = false)
    @Schema(description = "Whether the branch is currently active", example = "true")
    private Boolean isActive = true;
}
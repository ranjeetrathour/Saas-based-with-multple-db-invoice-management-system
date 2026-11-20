package com.moptra.invoice.client.entities;

import com.moptra.invoice.comman.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents optional branches/locations of a Client Company (e.g., Petco regional offices/hubs).
 * FUTURE CHANGE POINT: Add branchContactEmail or branchTaxId if branch-level invoices are generated.
 */
@Entity
@Table(
        name = "client_branches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_client_branch_company_code",
                        columnNames = {"client_company_id", "branch_code"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Optional branch belonging to a client company")
public class ClientBranch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_company_id", nullable = false)
    @Schema(description = "Parent client company")
    private ClientCompany clientCompany;

    @Column(name = "branch_name", nullable = false)
    @Schema(description = "Name of the client branch", example = "Petco West Hub")
    private String branchName;

    @Column(name = "branch_code", nullable = false)
    @Schema(description = "Branch code unique within the client company", example = "PETCO-WEST")
    private String branchCode;

    // Branch-specific GST / Tax number
    @Column(name = "gst_no")
    @Schema(description = "Branch-specific GST/tax number")
    private String gst;

    // Branch-specific billing address
    @Column(name = "billing_address", columnDefinition = "TEXT")
    @Schema(description = "Branch-specific billing address")
    private String billingAddress;

    @Schema(description = "Branch city", example = "Phoenix")
    private String city;

    @Schema(description = "Branch state", example = "Arizona")
    private String state;

    @Schema(description = "Branch country", example = "USA")
    private String country;

    @Column(name = "is_active", nullable = false)
    @Schema(description = "Whether the client branch is active", example = "true")
    private Boolean isActive = true;
}
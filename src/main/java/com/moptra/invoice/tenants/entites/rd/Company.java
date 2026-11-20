package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.tenants.enums.CompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents the primary SaaS Tenant / Service Provider (Vendor) company (e.g., Shyftlab).
 * FUTURE CHANGE POINT: Add billing currency, tax registration details, or subscription plan
 * fields here if vendor-level SaaS billing or multi-currency invoicing is introduced.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Main Tenant / Service Provider (Vendor) company onboarded on the SaaS platform")
public class Company extends BaseEntity {

    @Column(name = "company_name", nullable = false)
    @Schema(description = "Legal/display name of the vendor company", example = "Shyftlab")
    private String companyName;

    @Column(name = "company_code", nullable = false, unique = true)
    @Schema(description = "Unique short identifier code for tenant routing", example = "SHYFTLAB")
    private String companyCode;

    @Column(name = "company_email", nullable = false, unique = true)
    @Schema(description = "Primary official contact email of the vendor company", example = "admin@shyftlab.com")
    private String companyEmail;

    @Column(name = "company_phone")
    @Schema(description = "Primary contact phone number", example = "+91-9876543210")
    private String companyPhone;

    @Schema(description = "Official website URL", example = "https://shyftlab.com")
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Lifecycle status of the tenant company", example = "ACTIVE")
    private CompanyStatus status;
}
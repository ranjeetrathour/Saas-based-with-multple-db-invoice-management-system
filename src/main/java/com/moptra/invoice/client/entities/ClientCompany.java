package com.moptra.invoice.client.entities;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.tenants.entites.rd.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a Client Company onboarded by the Vendor/Tenant (e.g., Petco onboarded by Shyftlab).
 * Linked to Company (vendor) so client data remains strictly isolated per tenant.
 *
 * FUTURE CHANGE POINT: Add paymentTermsDays, defaultCurrency, or primaryContactName
 * if invoice due dates or currency rules are automated per client.
 */
@Entity
@Table(
        name = "client_companies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_client_company_tenant_code",
                        columnNames = {"company_id", "company_code"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client company onboarded under a vendor/tenant (e.g., Petco)")
public class ClientCompany extends BaseEntity {

    @Column(name = "company_name", nullable = false)
    @Schema(description = "Name of the client company", example = "Petco")
    private String companyName;

    @Column(name = "company_code", nullable = false)
    @Schema(description = "Client identifier code (unique within vendor)", example = "PETCO")
    private String companyCode;

    @Column(nullable = false)
    @Schema(description = "Primary contact or billing email of the client company", example = "billing@petco.com")
    private String email;

    @Schema(description = "Client company phone number", example = "+1-800-555-0199")
    private String phone;

    // Primary / Headquarter GST or tax number when no branch-specific number is used
    @Column(name = "gst_no")
    @Schema(description = "Primary GST/tax number of the client company", example = "27AABCP1234C1Z2")
    private String gst;

    // Primary billing address
    @Column(name = "billing_address", columnDefinition = "TEXT")
    @Schema(description = "Primary billing address for invoices")
    private String billingAddress;

    @Schema(description = "City of primary billing address", example = "San Diego")
    private String city;

    @Schema(description = "State of primary billing address", example = "California")
    private String state;

    @Schema(description = "Country of primary billing address", example = "USA")
    private String country;
}
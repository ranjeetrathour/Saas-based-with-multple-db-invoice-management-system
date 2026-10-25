package com.moptra.invoice.client.entities;

import com.moptra.invoice.comman.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity for Main Tenant's Client Companies (e.g., HCL Technologies)
 */
@Entity
@Table(name = "client_companies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCompany extends BaseEntity {

    @Column(name = "company_name", nullable = false)
    private String companyName; // e.g., HCL Technologies

    @Column(name = "company_code", nullable = false, unique = true)
    private String companyCode;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(name = "gst_no")
    private String gst; // Primary/Headquarter GST Number when no branch present

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress; // Primary Billing Address

    private String city;

    private String state;

    private String country;
}
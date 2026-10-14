package com.moptra.invoice.tenants.entites.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.tenants.enums.CompanyStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "companies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_code",nullable = false, unique = true)
    private String companyCode;

    @Column(name = "company_email", nullable = false, unique = true)
    private String companyEmail;

    @Column(name = "company_phone")
    private String companyPhone;

    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStatus status;
}

package com.moptra.invoice.email.entites;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.email.enums.EmailProviderType;
import com.moptra.invoice.tenants.entites.rd.Company;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenant_email_configs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantEmailConfig extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "app_password")
    private String appPassword;

    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_provider_type", nullable = false)
    private EmailProviderType emailProviderType;

    // Microsoft OAuth fields
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "refresh_token")
    private String refreshToken;
}

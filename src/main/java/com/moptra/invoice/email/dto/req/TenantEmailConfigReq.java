package com.moptra.invoice.email.dto.req;

import com.moptra.invoice.email.enums.EmailProviderType;
import com.moptra.invoice.user.enums.IdentityType;

public record TenantEmailConfigReq(
        String senderEmail,
        IdentityType identityType,
        EmailProviderType emailProviderType,

        // SMTP / Gmail
        String appPassword,
        String host,
        Integer port,

        // Microsoft OAuth
        String clientId,
        String clientSecret,
        String tenantId,
        String refreshToken
) {
}
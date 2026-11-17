package com.moptra.invoice.email.dto.req;

import com.moptra.invoice.email.enums.EmailProviderType;

public record TenantEmailConfigReq(
        String senderEmail, String appPassword, String host, String port, EmailProviderType emailProviderType
) {
};

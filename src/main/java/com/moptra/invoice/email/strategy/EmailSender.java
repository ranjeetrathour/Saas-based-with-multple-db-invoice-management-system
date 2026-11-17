package com.moptra.invoice.email.strategy;

import com.moptra.invoice.email.dto.req.EmailMessageDto;
import com.moptra.invoice.email.entites.TenantEmailConfig;
import com.moptra.invoice.email.enums.EmailProviderType;

public interface EmailSender {
    void sendEmail(TenantEmailConfig config, EmailMessageDto messageDto);

    // Yeh method factory ko batayega ki yeh implementation kis provider ke liye hai
    EmailProviderType getSupportedProvider();
}

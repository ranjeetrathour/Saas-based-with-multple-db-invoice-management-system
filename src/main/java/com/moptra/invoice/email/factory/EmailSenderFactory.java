package com.moptra.invoice.email.factory;

import com.moptra.invoice.email.enums.EmailProviderType;
import com.moptra.invoice.email.strategy.EmailSender;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EmailSenderFactory {

    private final Map<EmailProviderType, EmailSender> senderMap = new EnumMap<>(EmailProviderType.class);

    public EmailSenderFactory(@Nonnull List<EmailSender> senders) {

        log.info("Total EmailSender implementations found: {}", senders.size());

        for (EmailSender sender : senders) {

            EmailProviderType provider = sender.getSupportedProvider();
            log.info("Registering email sender: {} -> {}", sender.getClass().getSimpleName(), provider);

            if (provider == null) {
                throw new IllegalStateException("Email sender provider cannot be null: " + sender.getClass().getName());
            }

            if (senderMap.containsKey(provider)) {
                throw new IllegalStateException("Duplicate email sender for provider: " + provider);
            }

            senderMap.put(provider, sender);
        }

        log.info("Registered email providers: {}", senderMap.keySet());
    }

    public EmailSender getSender(EmailProviderType providerType) {

        if (providerType == null) {
            throw new IllegalArgumentException("Email provider type cannot be null");
        }

        EmailSender sender = senderMap.get(providerType);
        if (sender == null) {
            throw new IllegalArgumentException("Unsupported email provider type: " + providerType + ". Available providers: " + senderMap.keySet());
        }

        return sender;
    }
}
package com.moptra.invoice.email.strategy.impl;

import com.moptra.invoice.email.dto.req.EmailMessageDto;
import com.moptra.invoice.email.entites.TenantEmailConfig;
import com.moptra.invoice.email.enums.EmailProviderType;
import com.moptra.invoice.email.strategy.EmailSender;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
@AllArgsConstructor
public class GmailEmailSender implements EmailSender {
    @Override
    public void sendEmail(TenantEmailConfig config, EmailMessageDto messageDto) {
        JavaMailSender mailSender = createJavaMailSender(config);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(config.getSenderEmail());
        message.setTo(messageDto.getTo());
        message.setSubject(messageDto.getSubject());
        message.setText(messageDto.getBody());

        if (messageDto.getReplyTo() != null) {
            message.setReplyTo(messageDto.getReplyTo());
        }

        mailSender.send(message);
    }

    @Override
    public EmailProviderType getSupportedProvider() {
        return EmailProviderType.GMAIL;
    }

    private @Nonnull JavaMailSender createJavaMailSender(TenantEmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost()); // e.g., smtp.gmail.com
        mailSender.setPort(config.getPort()); // e.g., 587
        mailSender.setUsername(config.getSenderEmail());
        mailSender.setPassword(config.getAppPassword()); // Decrypt if encrypted

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }

}

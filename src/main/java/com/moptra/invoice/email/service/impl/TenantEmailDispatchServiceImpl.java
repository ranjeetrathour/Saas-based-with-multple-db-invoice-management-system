package com.moptra.invoice.email.service.impl;

import com.moptra.invoice.authentication.utils.SecurityUtils;
import com.moptra.invoice.comman.Constant.Constants;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.email.dto.req.EmailMessageDto;
import com.moptra.invoice.email.entites.TenantEmailConfig;
import com.moptra.invoice.email.enums.EmailProviderType;
import com.moptra.invoice.email.factory.EmailSenderFactory;
import com.moptra.invoice.email.repository.TenantEmailConfigRepository;
import com.moptra.invoice.email.strategy.EmailSender;
import com.moptra.invoice.email.service.TenantEmailDispatchService;
import com.moptra.invoice.tenants.context.TenantContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TenantEmailDispatchServiceImpl implements TenantEmailDispatchService {
    private final TenantEmailConfigRepository emailConfigRepository;
    private final EmailSenderFactory emailSenderFactory;

    public TenantEmailDispatchServiceImpl(TenantEmailConfigRepository emailConfigRepository, EmailSenderFactory emailSenderFactory) {
        this.emailConfigRepository = emailConfigRepository;
        this.emailSenderFactory = emailSenderFactory;
    }

    @Override
    public void triggerEmailForTenant(String companyId, EmailMessageDto messageDto) {
       var previousTenant = SecurityUtils.getCurrentUser().getTenantId();
        try{
            TenantContext.setTenantId(Constants.MASTER_TENANT);
            TenantEmailConfig config = emailConfigRepository.findByCompanyId(companyId)
                    .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Email config not found for company id: " + companyId));

            EmailProviderType providerType = config.getEmailProviderType();
            EmailSender sender = emailSenderFactory.getSender(providerType);
            sender.sendEmail(config, messageDto);
        }finally {
            TenantContext.clear();
            TenantContext.setTenantId(previousTenant);
        }

    }
}

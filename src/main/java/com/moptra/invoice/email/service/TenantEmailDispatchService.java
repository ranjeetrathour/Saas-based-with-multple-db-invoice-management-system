package com.moptra.invoice.email.service;

import com.moptra.invoice.email.dto.req.EmailMessageDto;

@FunctionalInterface
public interface TenantEmailDispatchService {
    void triggerEmailForTenant(String companyId, EmailMessageDto messageDto );
}

package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;

public interface CompanyRegistrationService {
    RegisterCompanyResponse register(RegisterCompanyRequest request);
}

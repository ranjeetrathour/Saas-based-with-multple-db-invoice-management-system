package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.user.entities.rd.User;

public interface TenantUserInitializerService {
     void createTenantAdminUser(String databaseName, RegisterCompanyRequest request);
     void createTenantUser(String databaseName, User request);
}

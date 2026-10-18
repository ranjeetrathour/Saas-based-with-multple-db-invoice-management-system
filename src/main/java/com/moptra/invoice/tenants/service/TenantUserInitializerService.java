package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.user.dto.req.CreateUserRequest;

public interface TenantUserInitializerService {
     void createTenantAdminUser(String databaseName, RegisterCompanyRequest request);
     void createTenantUser(String databaseName, CreateUserRequest request);
}

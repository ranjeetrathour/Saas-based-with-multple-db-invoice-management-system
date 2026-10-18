package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.user.dto.req.CreateUserRequest;
import com.moptra.invoice.user.entities.User;

public interface TenantUserInitializerService {
     void createTenantAdminUser(String databaseName, RegisterCompanyRequest request);
     void createTenantUser(String databaseName, CreateUserRequest request);
}

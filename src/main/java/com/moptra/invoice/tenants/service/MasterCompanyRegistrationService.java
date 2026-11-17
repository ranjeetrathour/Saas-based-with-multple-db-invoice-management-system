package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.entites.rd.TenantDatabase;
import com.moptra.invoice.user.entities.rd.UserIdentity;

public interface MasterCompanyRegistrationService {
    Company createCompany(RegisterCompanyRequest request);
    void markProvisioningFailed(String companyId, String tenantDatabaseId);
    TenantDatabase registerTenantDbDetails(Company company, String dbName);
    UserIdentity createUserIdentityInMasterDbForLogin(Company company, RegisterCompanyRequest request);
}

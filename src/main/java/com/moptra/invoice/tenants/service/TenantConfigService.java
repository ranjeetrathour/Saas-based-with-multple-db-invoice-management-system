package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.entites.TenantDatabase;

public interface TenantConfigService {
    void loadAndRegisterAllTenants();
    void registerNewTenant(TenantDatabase tenantDatabase);
}

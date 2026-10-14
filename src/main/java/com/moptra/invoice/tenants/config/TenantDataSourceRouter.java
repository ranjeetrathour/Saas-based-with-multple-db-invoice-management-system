package com.moptra.invoice.tenants.config;

import com.moptra.invoice.tenants.context.TenantContext;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantDataSourceRouter extends AbstractRoutingDataSource {

    private final Map<Object, DataSource> tenantDataSources = new ConcurrentHashMap<>();
    private final Map<Object, Object> rawTargetMap = new ConcurrentHashMap<>();
    private volatile DataSource fallbackDataSource;

    public void registerTenant(String tenantId, DataSource dataSource) {
        tenantDataSources.put(tenantId, dataSource);
        rawTargetMap.put(tenantId, dataSource);
        setTargetDataSources(rawTargetMap);
        afterPropertiesSet();
    }


    public void setDefaultTenant(String tenantId) {
        DataSource ds = tenantDataSources.get(tenantId);
        if (ds == null) {
            throw new IllegalStateException("Cannot set default tenant — [" + tenantId + "] is not registered");
        }
        this.fallbackDataSource = ds;
        setDefaultTargetDataSource(ds);
        afterPropertiesSet();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        System.out.println("DEBUG: Looking up DataSource for Tenant Key: " + lookupKey);

        DataSource ds = lookupKey != null ? tenantDataSources.get(lookupKey) : null;
        if (ds == null) {
            System.err.println("WARNING: Tenant DataSource NOT FOUND for key [" + lookupKey + "]. Falling back to master/default DB!");
            ds = fallbackDataSource;
        } else {
            System.out.println("SUCCESS: Found tenant DataSource for key [" + lookupKey + "]");
        }

        if (ds == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        return ds;
    }

    @Override
    protected @Nullable Object determineCurrentLookupKey() {
        Object tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : "master";
    }
}
package com.moptra.invoice.user.service;


import com.mongodb.client.MongoClient;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.user.entities.nrd.TenantConfigEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantMongoProvisioningServiceImpl {

    private final MongoClient mongoClient;

    /**
     * Creates test data and provisions the database dynamically
     * by targeting the specified database name via MongoTemplate.
     */
    public TenantConfigEntity provisionAndInsertTestData(String dataBaseName, String tenantCode, String companyName) {
        try {
            // Dynamically instantiate MongoTemplate pointing to the specific tenant database name
            MongoTemplate tenantTemplate = new MongoTemplate(mongoClient, TenantContext.getTenantId());

            // Construct test data entity
            TenantConfigEntity testConfig = TenantConfigEntity.builder()
                    .tenantCode(tenantCode)
                    .companyName(companyName)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Saving via MongoTemplate automatically creates the database and collection if they don't exist
            TenantConfigEntity savedEntity = tenantTemplate.save(testConfig, "tenant_configs");

            log.info("Successfully provisioned MongoDB database '{}' and saved test config for tenant: {}", dataBaseName, tenantCode);
            return savedEntity;

        } catch (Exception e) {
            log.error("Failed to provision and insert test data for database: {}", dataBaseName, e);
            throw new RuntimeException("MongoDB provisioning failed", e);
        }
    }

    /**
     * Example method to fetch data from a specific tenant database dynamically
     */
    public List<TenantConfigEntity> getAllConfigsForTenant(String dataBaseName) {
        MongoTemplate tenantTemplate = new MongoTemplate(mongoClient, dataBaseName);
        return tenantTemplate.findAll(TenantConfigEntity.class, "tenant_configs");
    }
}
package com.moptra.invoice.tenants.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.moptra.invoice.tenants.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Slf4j
public class TenantRoutingMongoDatabaseFactory extends SimpleMongoClientDatabaseFactory {

    private final MongoClient mongoClient;

    public TenantRoutingMongoDatabaseFactory(MongoClient mongoClient, String defaultDatabaseName) {
        super(mongoClient, defaultDatabaseName);
        this.mongoClient = mongoClient;
    }

    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        String tenantId = TenantContext.getTenantId();
        String targetDbName;

        if (tenantId != null && !tenantId.isBlank()) {
            targetDbName = tenantId.trim().toLowerCase();

            if (!targetDbName.startsWith("tenant_")) {
                targetDbName = "tenant_" + targetDbName;
            }
        } else {
            targetDbName = "default_invoice_db";
        }

        log.info("MONGO ROUTER: TenantContext={}, Database={}", tenantId, targetDbName);

        return mongoClient.getDatabase(targetDbName);
    }
}

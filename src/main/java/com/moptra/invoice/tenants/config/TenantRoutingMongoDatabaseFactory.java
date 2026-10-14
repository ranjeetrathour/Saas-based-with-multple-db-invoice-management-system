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


    /**
     *
     * @param dbName must not be {@literal null} or empty.
     * @return run on each db hit
     */
    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        Object tenantId = TenantContext.getTenantId();

        String targetDbName = (tenantId != null && !tenantId.toString().isEmpty())
                ? "tenant_" + tenantId.toString().toLowerCase()
                : "default_invoice_db";

        log.debug("MONGO ROUTER: Routing request to database -> {}", targetDbName);

        return this.mongoClient.getDatabase(targetDbName);
    }
}
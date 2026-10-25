
package com.moptra.invoice.comman.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.moptra.invoice.tenants.config.TenantRoutingMongoDatabaseFactory;
import com.moptra.invoice.tenants.properties.TenantProvisioningProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {
                "com.moptra.invoice.timesheet.repository"
        },
        mongoTemplateRef = "mongoTemplate"
)
@RequiredArgsConstructor
public class MongoJpaConfiguration {

    private final TenantProvisioningProperties tenantProvisioningProperties;

    @Bean
    public MongoClient mongoClient() {
        // Build connection string dynamically from your properties
        String host = tenantProvisioningProperties.getMongoHost();
        int port = tenantProvisioningProperties.getMongoPort();

        // If username/password are provided in the future, you can conditionally build a secure URI
        // Example: mongodb://username:password@host:port
        String connectionString = String.format("mongodb://%s:%d", host, port);

        return MongoClients.create(connectionString);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new TenantRoutingMongoDatabaseFactory(mongoClient, "default_invoice_db");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}

package com.moptra.invoice.tenants.provisioning;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TenantMongoProvisioningService {
    private final MongoClient mongoClient;

    public void createDataBase(String dataBaseName) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);

        try {
            // Document insert karne se MongoDB physical database create kar dega
            database.getCollection("_init_metadata")
                    .insertOne(new Document("status", "initialized"));
            log.info("Database and initial metadata created: {}", dataBaseName);
        } catch (Exception e) {
            log.error("Failed to initialize database: {}", dataBaseName, e);
            throw new RuntimeException(e);
        }
    }
}
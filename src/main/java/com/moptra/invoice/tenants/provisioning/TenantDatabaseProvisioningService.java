package com.moptra.invoice.tenants.provisioning;

import com.moptra.invoice.tenants.properties.TenantProvisioningProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Service used to manage tenant database creation and schema migrations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantDatabaseProvisioningService {

    private final TenantProvisioningProperties properties;

    public void createDatabase(String databaseName) {
        String url = "jdbc:postgresql://"
                + properties.getHost()
                + ":"
                + properties.getPort()
                + "/"
                + properties.getAdminDatabase();

        try (Connection connection = DriverManager.getConnection(url, properties.getUsername(), properties.getPassword());
             Statement statement = connection.createStatement()) {
            String sql = "CREATE DATABASE " + databaseName;
            statement.executeUpdate(sql);
            log.info("database created {} ",databaseName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tenant database: " + databaseName, e);
        }
    }

    public void migrateDatabase(String databaseName) {
        String tenantUrl = "jdbc:postgresql://"
                + properties.getHost()
                + ":"
                + properties.getPort()
                + "/"
                + databaseName;

       try {
           Flyway flyway = Flyway.configure()
                   .dataSource(tenantUrl, properties.getUsername(), properties.getPassword())
                   .locations("classpath:tenant", "classpath:db/migration/tenant")
                   .baselineOnMigrate(true)
                   .validateOnMigrate(true)
                   .outOfOrder(true)
                   .load();

           flyway.migrate();
           log.info("migration success for db {} ", databaseName);
       } catch (Exception e) {
           log.info("migration failed after creating db");
           throw new RuntimeException(e);
       }
    }
}
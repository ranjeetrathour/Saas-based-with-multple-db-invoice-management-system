package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.tenants.config.TenantDataSourceRouter;
import com.moptra.invoice.tenants.entites.rd.TenantDatabase;
import com.moptra.invoice.tenants.repository.rd.TenantDatabaseRepository;
import com.moptra.invoice.tenants.service.TenantConfigService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class TenantConfigServiceImpl implements TenantConfigService {

    private final TenantDatabaseRepository tenantDatabaseRepository;
    private final TenantDataSourceRouter tenantDataSourceRouter;

    @Override
    public void loadAndRegisterAllTenants() {
        List<TenantDatabase> all = tenantDatabaseRepository.findAll();
        all.stream()
                .filter(Objects::nonNull)
                .forEach(this::buildMigrateAndRegister);
    }

    @Override
    public void registerNewTenant(TenantDatabase tenantDatabase) {
        buildMigrateAndRegister(tenantDatabase);
    }

    private void buildMigrateAndRegister(TenantDatabase tenantDatabase) {
        DataSource dataSource = buildHikariDataSource(tenantDatabase);

        // 1. Run migrations to create 'users' and other tenant tables
        migrateTenant(dataSource, tenantDatabase.getDatabaseName());

        // 2. Register datasource using databaseName as key (matching TenantContext)
        tenantDataSourceRouter.registerTenant(tenantDatabase.getDatabaseName(), dataSource);
    }

    private @NonNull DataSource buildHikariDataSource(@NonNull TenantDatabase tenantDatabase) {
        HikariConfig dataSource = new HikariConfig();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/" + tenantDatabase.getDatabaseName());
        dataSource.setUsername(tenantDatabase.getUsername());
        dataSource.setPassword(tenantDatabase.getPassword());
        dataSource.setPoolName("HikariPool-" + tenantDatabase.getDatabaseName());
        dataSource.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(dataSource);
    }

    private void migrateTenant(DataSource ds, String tenantId) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .locations("classpath:tenant", "classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();

        log.info("Migration completed for tenant [{}]", tenantId);
    }
}
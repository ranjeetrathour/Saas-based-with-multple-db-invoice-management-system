package com.moptra.invoice.comman.config;

import com.moptra.invoice.tenants.config.TenantDataSourceRouter;
import com.moptra.invoice.tenants.service.TenantConfigService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DataSourceConfig {

    @Bean(name = "rdDataSource")
    public DataSource dataSource(DataSource masterDataSource) {
        TenantDataSourceRouter tenantDataSourceRouter = new TenantDataSourceRouter();
        tenantDataSourceRouter.registerTenant("master", masterDataSource);
        tenantDataSourceRouter.setDefaultTenant("master");
        return tenantDataSourceRouter;
    }

    @Bean
    public ApplicationRunnerTenantLoader tenantLoader(TenantConfigService tenantConfigService) {
        return new ApplicationRunnerTenantLoader(tenantConfigService);
    }

    public static class ApplicationRunnerTenantLoader
            implements org.springframework.boot.ApplicationRunner {

        private final TenantConfigService tenantConfigService;

        public ApplicationRunnerTenantLoader(TenantConfigService tenantConfigService) {
            this.tenantConfigService = tenantConfigService;
        }

        @Override
        public void run(org.springframework.boot.ApplicationArguments args) {
            tenantConfigService.loadAndRegisterAllTenants();
        }
    }
}
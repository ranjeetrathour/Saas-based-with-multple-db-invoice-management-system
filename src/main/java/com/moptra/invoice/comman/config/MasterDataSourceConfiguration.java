package com.moptra.invoice.comman.config;

import com.moptra.invoice.comman.properties.MasterDataSourceConfigProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MasterDataSourceConfiguration {

    private final MasterDataSourceConfigProperties properties;

    public MasterDataSourceConfiguration(MasterDataSourceConfigProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {

        HikariConfig config = new HikariConfig();
        config.setPoolName("HikariPool-master");
        config.setJdbcUrl(properties.getUrl());
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setDriverClassName(properties.getDriverClassName());

        HikariDataSource ds = new HikariDataSource(config);

        Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration/master")
                .baselineOnMigrate(true)
                .load()
                .migrate();

        return ds;
    }
}

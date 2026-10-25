package com.moptra.invoice.comman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.moptra.invoice.tenants.repository",
                "com.moptra.invoice.user.repository",
                "com.moptra.invoice.client.repository",
                "com.moptra.invoice.project.repository",
        },
        entityManagerFactoryRef = "rdEntityManagerFactory",
        transactionManagerRef = "rdTransactionManager"
)

public class JpaConfiguration {

    @Autowired
    @Qualifier("rdDataSource")
    private DataSource dataSource;
    @Autowired
    private JpaProperties jpaProperties;


    @Bean
    public LocalContainerEntityManagerFactoryBean rdEntityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(
                "com.moptra.invoice.tenants.entites.rd",
                "com.moptra.invoice.user.entities.rd",
                "com.moptra.invoice.client.entities",
                "com.moptra.invoice.project.entites"
        );
        Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
        jpaPropertiesMap.put("hibernate.ddl-auto", "validate");
        jpaPropertiesMap.put("hibernate.format_sql", "true");

        jpaPropertiesMap.put("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");

        factory.setJpaPropertyMap(jpaPropertiesMap);
        return factory;
    }

    @Bean
    public PlatformTransactionManager rdTransactionManager() {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(rdEntityManagerFactory().getObject());
        txManager.setValidateExistingTransaction(true);
        return txManager;
    }
}
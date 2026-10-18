package com.moptra.invoice.tenants.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * this is used to create default database in same server for each tenants
 */
@Data
@ConfigurationProperties(prefix = "app.tenant.provisioning")
@Component
public class TenantProvisioningProperties {

    private String host;

    private Integer port;

    private String adminDatabase;

    private String username;

    private String password;

    private String mongoHost;

    private Integer mongoPort;
}

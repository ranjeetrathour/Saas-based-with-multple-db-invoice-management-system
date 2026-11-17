CREATE TABLE IF NOT EXISTS tenant_email_configs(
    id VARCHAR(36) PRIMARY KEY,
    company_id VARCHAR(255) NOT NULL,
    sender_email VARCHAR(255) NOT NULL,
    app_password VARCHAR(255) ,
    host VARCHAR(255) NOT NULL,
    port INT NOT NULL,
    client_id VARCHAR(255),
    client_secret VARCHAR(255),
    tenant_id VARCHAR(255),
    refresh_token VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    email_provider_type VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tenant_configs_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE INDEX idx_tenant_configs_company
    ON tenant_email_configs(company_id);
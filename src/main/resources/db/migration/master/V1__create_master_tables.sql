CREATE TABLE IF NOT EXISTS companies (
                                         id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS user_identities (
   id VARCHAR(36) PRIMARY KEY,
    company_id VARCHAR(36),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    identity_type VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_identities_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL
    );

CREATE INDEX idx_user_identities_email ON user_identities(email);
CREATE INDEX idx_user_identities_company_id ON user_identities(company_id);

CREATE TABLE tenant_databases (
                                  id VARCHAR(36) PRIMARY KEY,
                                  company_id VARCHAR(36) NOT NULL UNIQUE,
                                  database_name VARCHAR(255) NOT NULL UNIQUE,
                                  host VARCHAR(255) NOT NULL,
                                  port INTEGER NOT NULL,
                                  username VARCHAR(255) NOT NULL,
                                  password VARCHAR(255) NOT NULL,
                                  status VARCHAR(50) NOT NULL, -- Added comma here

                                  CONSTRAINT fk_tenant_database_company
                                      FOREIGN KEY (company_id)
                                          REFERENCES companies(id)
);
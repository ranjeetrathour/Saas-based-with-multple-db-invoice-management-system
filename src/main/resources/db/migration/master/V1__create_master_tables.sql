-- ============================================================================
-- MASTER DATABASE DDL
-- Holds SaaS tenant registry, vendor branches, DB routing metadata, and
-- authentication identities (for both Vendor users and Client Portal users).
--
-- NOTE: client_company_id in user_identities references client_companies.id
-- in the respective Tenant DB (no cross-DB SQL FK constraint).
-- ============================================================================

CREATE TABLE IF NOT EXISTS companies (
                                         id VARCHAR(36) PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    company_code VARCHAR(255) NOT NULL UNIQUE,
    company_email VARCHAR(255) NOT NULL UNIQUE,
    company_phone VARCHAR(50),
    website VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

CREATE TABLE IF NOT EXISTS branches (
                                        id VARCHAR(36) PRIMARY KEY,
    company_id VARCHAR(36) NOT NULL,
    branch_name VARCHAR(255) NOT NULL,
    branch_code VARCHAR(255) NOT NULL UNIQUE,
    gst_no VARCHAR(100) NOT NULL,
    branch_address TEXT,
    branch_city VARCHAR(100),
    branch_country VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_branch_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS tenant_databases (
                                                id VARCHAR(36) PRIMARY KEY,
    company_id VARCHAR(36) NOT NULL UNIQUE,
    database_name VARCHAR(255) NOT NULL UNIQUE,
    host VARCHAR(255) NOT NULL,
    port INT NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_tenant_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
    );

-- ISOLATION RULE:
-- - Vendor/Tenant user: company_id IS NOT NULL, client_company_id IS NULL
-- - Restricted Client Portal user: company_id IS NOT NULL, client_company_id IS NOT NULL
CREATE TABLE IF NOT EXISTS user_identities (
    id VARCHAR(36) PRIMARY KEY,
    company_id VARCHAR(36),
    client_company_id VARCHAR(36), -- Logical reference to Tenant DB client_companies.id
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    identity_type VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_identities_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL,
    CONSTRAINT uk_user_identities_company_client_email UNIQUE (company_id, client_company_id, email)
    );

CREATE INDEX IF NOT EXISTS idx_user_identities_email ON user_identities(email);
CREATE INDEX IF NOT EXISTS idx_user_identities_company_id ON user_identities(company_id);
CREATE INDEX IF NOT EXISTS idx_user_identities_client_company_id ON user_identities(client_company_id);
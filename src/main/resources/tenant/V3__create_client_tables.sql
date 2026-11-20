-- ============================================================================
-- TENANT DATABASE: CLIENT COMPANY & BRANCH TABLES (e.g., Petco)
-- Runs inside tenant_<company_code> (e.g., tenant_shyftlab), where data is
-- already isolated per vendor tenant.
--
-- FUTURE CHANGE POINT: Add payment_terms_days or default_currency if needed
-- for automated invoice due dates and multi-currency client billing.
-- ============================================================================

CREATE TABLE IF NOT EXISTS client_companies (
    id VARCHAR(36) PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    company_code VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    gst_no VARCHAR(50),
    billing_address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

CREATE TABLE IF NOT EXISTS client_branches (
    id VARCHAR(36) PRIMARY KEY,
    client_company_id VARCHAR(36) NOT NULL,
    branch_name VARCHAR(255) NOT NULL,
    branch_code VARCHAR(100) NOT NULL,
    gst_no VARCHAR(50),
    billing_address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_branch_company FOREIGN KEY (client_company_id) REFERENCES client_companies(id) ON DELETE CASCADE,
    CONSTRAINT uk_client_branch_company_code UNIQUE (client_company_id, branch_code)
    );

CREATE INDEX IF NOT EXISTS idx_client_branches_client_company_id ON client_branches(client_company_id);
-- Create projects table
CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    project_code VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    client_company_id VARCHAR(36) NOT NULL,
    branch_id VARCHAR(36),
    billing_type VARCHAR(50) NOT NULL,
    total_budget NUMERIC(12, 2),
    currency VARCHAR(10) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_project_client_company_id FOREIGN KEY (client_company_id) REFERENCES client_companies(id) ON DELETE CASCADE
    );

-- Create project_allocations table
CREATE TABLE IF NOT EXISTS project_allocations (
    id VARCHAR(36) PRIMARY KEY,
    employee_id VARCHAR(36) NOT NULL,
    project_id VARCHAR(36) NOT NULL,
    billing_hourly_rate NUMERIC(10, 2) NOT NULL,
    start_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_project_allocation_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    );

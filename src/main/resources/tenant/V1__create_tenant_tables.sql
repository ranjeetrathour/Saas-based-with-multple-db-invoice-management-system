-- Clean Tenant User schema WITHOUT password_hash
CREATE TABLE IF NOT EXISTS users (
                                     id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    employee_id VARCHAR(36),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS employees (
                                             id VARCHAR(36) PRIMARY KEY,
    employee_id VARCHAR(255) NOT NULL UNIQUE,
    designation VARCHAR(255),
    department VARCHAR(255),
    manager_id VARCHAR(36),
    team_lead_id VARCHAR(36),
    branch_id VARCHAR(36),
    joining_date DATE,
    hourly_cost NUMERIC(10, 2),
    user_id VARCHAR(36) UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_employee_manager FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL,
    CONSTRAINT fk_employee_team_lead FOREIGN KEY (team_lead_id) REFERENCES employees(id) ON DELETE SET NULL
    );

-- Circular foreign key binding for OneToOne linkage (Fixed reference to 'employees')
ALTER TABLE users
    ADD CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL;

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_employee_user_id ON employees(user_id);
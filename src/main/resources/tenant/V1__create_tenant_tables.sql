CREATE TABLE IF NOT EXISTS user_roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    is_system_role BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_roles_name UNIQUE (name)
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
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role_id VARCHAR(36) NOT NULL,
    employee_id VARCHAR(36),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );


INSERT INTO user_roles (
    id,
    name,
    description,
    is_system_role,
    created_at,
    updated_at
)
VALUES
    ('role_company_admin', 'COMPANY_ADMIN', 'Full vendor tenant administration, client onboarding, and role/user management', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_hr', 'HR', 'Employee onboarding, reporting hierarchy, and branch assignment', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_manager', 'MANAGER', 'Reviews and approves weekly employee timesheets', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_team_lead', 'TEAM_LEAD', 'Technical/team lead reviewing weekly work logs and timesheets', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_employee', 'EMPLOYEE', 'Fills and submits weekly timesheets for assigned client work', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_finance', 'FINANCE', 'Calculates employee billing from approved hours, sends to client, and verifies payment receipt', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_client_admin', 'CLIENT_ADMIN', 'Restricted client portal administrator for client company scope', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_client_reviewer', 'CLIENT_REVIEWER', 'Restricted client user reviewing hours/work acknowledgement', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('role_client_finance', 'CLIENT_FINANCE', 'Restricted client finance user acknowledging and verifying billing', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 5. ADD FOREIGN KEY CONSTRAINTS
-- =====================================================

-- USERS -> USER_ROLES

ALTER TABLE users ADD CONSTRAINT fk_users_user_role FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE RESTRICT;

-- USERS -> EMPLOYEES

ALTER TABLE users ADD CONSTRAINT fk_users_employee
        FOREIGN KEY (employee_id)
            REFERENCES employees(id)
            ON DELETE SET NULL;

-- EMPLOYEES -> EMPLOYEES (MANAGER)

ALTER TABLE employees
    ADD CONSTRAINT fk_employee_manager
        FOREIGN KEY (manager_id)
            REFERENCES employees(id)
            ON DELETE SET NULL;

-- EMPLOYEES -> EMPLOYEES (TEAM LEAD)

ALTER TABLE employees
    ADD CONSTRAINT fk_employee_team_lead
        FOREIGN KEY (team_lead_id)
            REFERENCES employees(id)
            ON DELETE SET NULL;

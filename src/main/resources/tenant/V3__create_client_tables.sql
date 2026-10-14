CREATE TABLE client_companies (
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

CREATE TABLE client_branches (
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
       CONSTRAINT fk_client_branch_company FOREIGN KEY (client_company_id) REFERENCES client_companies(id) ON DELETE CASCADE
);
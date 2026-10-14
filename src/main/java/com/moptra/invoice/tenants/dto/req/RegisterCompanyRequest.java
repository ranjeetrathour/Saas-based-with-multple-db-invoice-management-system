package com.moptra.invoice.tenants.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * while register company one initial user will be created whose details will be inserted into main db
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCompanyRequest {
    // --- 1. Company Details ---
    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Company code is required")
    private String companyCode;

    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid email format")
    private String companyEmail;

    private String companyPhone;
    private String website;

    // --- 2. Branches (GST & Billing Addresses purpose) ---
    @NotEmpty(message = "At least one branch is required")
    @Valid
    private List<BranchRequest> branches;

    // --- 3. Initial Admin User (Dashboard Login Credentials) ---
    @NotBlank(message = "Admin name is required")
    private String adminName;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid email format")
    private String adminEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}

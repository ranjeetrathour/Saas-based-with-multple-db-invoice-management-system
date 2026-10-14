package com.moptra.invoice.tenants.repository.rd;

import com.moptra.invoice.tenants.entites.rd.Company;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company , String> {
    boolean existsByCompanyEmail(@NotBlank @Email String companyEmail);
}

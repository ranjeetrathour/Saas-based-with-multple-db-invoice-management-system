package com.moptra.invoice.tenants.repository;

import com.moptra.invoice.tenants.entites.rd.Company;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company , String> {
    boolean existsByCompanyEmail(@NotBlank @Email String companyEmail);

    boolean findByCompanyName(@NotBlank(message = "Company name is required") String companyName);

    boolean existsByCompanyCode(@NotBlank(message = "Company code is required") String s);
}

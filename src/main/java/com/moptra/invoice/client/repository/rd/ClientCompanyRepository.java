package com.moptra.invoice.client.repository.rd;

import com.moptra.invoice.client.entity.rd.ClientCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientCompanyRepository extends JpaRepository<ClientCompany, String> {
    boolean existsByCompanyCode(String companyCode);
    boolean existsByEmail(String email);
    Optional<ClientCompany> findByCompanyCode(String companyCode);
}
package com.moptra.invoice.email.repository;

import com.moptra.invoice.email.entites.TenantEmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantEmailConfigRepository extends JpaRepository<TenantEmailConfig, String> {
    Optional<TenantEmailConfig> findByCompanyId(String companyId);
}

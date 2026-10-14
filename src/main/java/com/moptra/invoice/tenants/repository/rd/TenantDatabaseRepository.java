package com.moptra.invoice.tenants.repository.rd;

import com.moptra.invoice.tenants.entites.rd.TenantDatabase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TenantDatabaseRepository extends JpaRepository<TenantDatabase , String> {
    Optional<TenantDatabase> findByCompanyId(Long companyId);
}

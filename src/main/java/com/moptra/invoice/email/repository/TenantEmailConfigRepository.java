package com.moptra.invoice.email.repository;

import com.moptra.invoice.email.entites.TenantEmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantEmailConfigRepository extends JpaRepository<TenantEmailConfig, String> {
    Optional<TenantEmailConfig> findByCompanyId(String companyId);

    @Query(nativeQuery = true, value = "SELECT * FROM tenant_email_configs WHERE company_id =?1 and  identity_type =?2 and client_company_id =?3 ")
    Optional<TenantEmailConfig> findByCompanyIdAndIdentityTypeAndClientCompanyId(String companyId, String identityType, String clientCompanyId);
}

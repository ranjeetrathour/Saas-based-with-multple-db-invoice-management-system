package com.moptra.invoice.authentication.service;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.roles.repository.UserRoleRepository;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.repository.UserIdentityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserIdentityRepository userIdentityRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserIdentity identity = userIdentityRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String tenantId = "master";
        String companyId = null;

        if (identity.getCompany() != null) {
            companyId = identity.getCompany().getId();
            String sanitizedCode = identity.getCompany().getCompanyCode()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "_");
            tenantId = "tenant_" + sanitizedCode;
        }

        UserRole userRole;
        String previousTenant = TenantContext.getTenantId();

        try {
            TenantContext.setTenantId(tenantId);
            log.info("Loading role for tenant: {}", tenantId);
            userRole = userRoleRepository.findById(identity.getRole()).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Role not found: " + identity.getRole()));

            log.info("Loaded role: {}", userRole.getName());

        } catch (Exception ex) {
            log.error("Failed to load role for tenant: {}", tenantId, ex);
            throw ex;

        } finally {
            if (previousTenant == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(previousTenant);
            }
        }

        return new CustomUserDetails(
                identity.getEmail(),
                identity.getPasswordHash(),
                tenantId,
                companyId,
                userRole
        );
    }
}
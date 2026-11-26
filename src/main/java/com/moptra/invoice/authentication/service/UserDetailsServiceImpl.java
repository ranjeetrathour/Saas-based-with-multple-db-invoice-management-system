package com.moptra.invoice.authentication.service;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.roles.service.UserRoleService;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.repository.UserIdentityRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserIdentityRepository userIdentityRepository;
        private final UserRoleService userRoleService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserIdentity identity = userIdentityRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String tenantId = resolveTenantId(identity);
        String companyId = identity.getCompany() != null ? identity.getCompany().getId() : null;

        UserRole userRole;
        String previousTenant = TenantContext.getTenantId();

        try {
            TenantContext.setTenantId(tenantId);
            log.info("Loading role for tenant: {}", tenantId);
            var userRoleResponse = userRoleService.getRoleById(identity.getRole());
           userRole =  GenericMapper.map(userRoleResponse, UserRole.class);
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

    private @NonNull String resolveTenantId(@NonNull UserIdentity identity) {

        if (identity.getCompany() == null) {
            return "master";
        }

        String sanitizedCode = identity.getCompany()
                .getCompanyCode()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "_");

        return "tenant_" + sanitizedCode;
    }
}
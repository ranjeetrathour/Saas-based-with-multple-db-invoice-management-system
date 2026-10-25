package com.moptra.invoice.authentication.service;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.repository.UserIdentityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserIdentity identity = userIdentityRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String tenantId = "master";
        String companyId = null;

        if (identity.getCompany() != null) {
            companyId = identity.getCompany().getId();
            String sanitizedCode = identity.getCompany().getCompanyCode()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "_");
            tenantId = "tenant_" + sanitizedCode;
        }

        return new CustomUserDetails(
                identity.getEmail(),
                identity.getPasswordHash(),
                tenantId,
                companyId,
                identity.getRole()
        );
    }
}
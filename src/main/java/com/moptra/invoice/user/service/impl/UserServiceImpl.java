package com.moptra.invoice.user.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.authentication.service.JwtService;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.entites.Company;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.tenants.repository.rd.CompanyRepository;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.dto.req.CreateUserRequest;
import com.moptra.invoice.user.entities.User;
import com.moptra.invoice.user.entities.UserIdentity;
import com.moptra.invoice.user.repository.rd.UserIdentityRepository;
import com.moptra.invoice.user.repository.rd.UserRepository;
import com.moptra.invoice.user.service.UserService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, String> implements UserService {

    private final UserIdentityRepository userIdentityRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantUserInitializerService tenantUserInitializerService;
    public UserServiceImpl(JpaRepository<User, String> repository, UserIdentityRepository userIdentityRepository, UserRepository userRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TenantUserInitializerService tenantUserInitializerService) {
        super(repository);
        this.userIdentityRepository = userIdentityRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tenantUserInitializerService = tenantUserInitializerService;
    }

    @Transactional
    @Override
    public void createUser(CreateUserRequest request) {
        // 1. Get currently logged-in Admin's details from SecurityContext
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();


        Company company = companyRepository.findById(currentUser.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 2. Save UserIdentity in Master DB (for global login lookup)
        UserIdentity identity = UserIdentity.builder()
                .company(company)
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(AccountStatus.ACTIVE)
                .build();

        userIdentityRepository.save(identity);

        // 3. Save domain User in current Tenant DB
        String tenantId = currentUser.getTenantId();
        try {
            TenantContext.setTenantId(tenantId); // Scope to Tenant DB
        tenantUserInitializerService.createTenantUser(tenantId, request);
        } finally {
            // Keep existing tenantId context active for request lifetime
            TenantContext.setTenantId(tenantId);
        }
    }
}

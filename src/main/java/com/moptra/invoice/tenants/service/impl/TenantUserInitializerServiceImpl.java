package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.dto.req.CreateUserRequest;
import com.moptra.invoice.user.entities.User;
import com.moptra.invoice.user.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TenantUserInitializerServiceImpl extends BaseServiceImpl<User, String> implements TenantUserInitializerService {


    public TenantUserInitializerServiceImpl(JpaRepository<User, String> repository) {
        super(repository);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTenantAdminUser(String databaseName, RegisterCompanyRequest request) {
        log.info("Executing save inside REQUIRES_NEW transaction for tenant DB context: {}", TenantContext.getTenantId());
        User tenantUser = User.builder()
                .name(request.getAdminName())
                .email(request.getAdminEmail())
                .role(UserRole.COMPANY_ADMIN)
                .build();

        save(tenantUser);
        log.info("Successfully saved user in tenant DB: {}", databaseName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTenantUser(String databaseName, CreateUserRequest request) {
        User tenantUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .build();
        save(tenantUser);
    }
}

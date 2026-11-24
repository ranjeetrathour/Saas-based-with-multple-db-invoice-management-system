package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.comman.Constant.Constants;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.roles.repository.UserRoleRepository;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.entities.rd.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TenantUserInitializerServiceImpl extends BaseServiceImpl<User, String> implements TenantUserInitializerService {

    private final UserRoleRepository userRoleRepository;

    public TenantUserInitializerServiceImpl(JpaRepository<User, String> repository ,UserRoleRepository userRoleRepository) {
        super(repository);
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTenantAdminUser(String databaseName, @NonNull RegisterCompanyRequest request) {
        log.info("Executing save inside REQUIRES_NEW transaction for tenant DB context: {}", TenantContext.getTenantId());
        String tenantDBName = TenantContext.getTenantId();
        UserRole userRole;
        try {
            TenantContext.setTenantId(Constants.MASTER_TENANT);
            userRole = userRoleRepository.fetchCompanyAdminRoleData().orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Role not found: COMPANY_ADMIN"));
        } finally {
            TenantContext.clear();
            TenantContext.setTenantId(tenantDBName);
        }
        User tenantUser = User.builder()
                .name(request.adminName())
                .email(request.adminEmail())
                .role(userRole)
                .build();

        save(tenantUser);
        log.info("Successfully saved user in tenant DB: {}", databaseName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTenantUser(String databaseName, User request) {
        save(request);
    }
}

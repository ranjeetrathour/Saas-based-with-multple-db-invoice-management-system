package com.moptra.invoice.user.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.entities.rd.Employee;
import com.moptra.invoice.user.entities.rd.User;
import com.moptra.invoice.user.service.MasterUserIdentityService;
import com.moptra.invoice.user.service.UserService;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, String> implements UserService {


    private final MasterUserIdentityService masterUserIdentityService;
    private final TenantUserInitializerService tenantUserInitializerService;

    public UserServiceImpl(JpaRepository<User, String> repository,MasterUserIdentityService masterUserIdentityService, TenantUserInitializerService tenantUserInitializerService) {
        super(repository);
        this.masterUserIdentityService = masterUserIdentityService;
        this.tenantUserInitializerService = tenantUserInitializerService;
    }

    @Override
    public void createUser(@NonNull CreateEmployeeRequest request) {
        // 1. Get currently logged-in Admin's details from SecurityContext
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        try {
           TenantContext.setTenantId("master");
           masterUserIdentityService.createMasterUserIdentity(currentUser.getCompanyId(), request);
        }finally {
            TenantContext.setTenantId(currentUser.getTenantId());
        }

        // 3. Map DTO to Entities cleanly using GenericMapper
        Employee employee = GenericMapper.map(request, Employee.class);
        User user = GenericMapper.map(request, User.class);
        user.setEmployee(employee);
        employee.setUser(user);
        String tenantId = currentUser.getTenantId();
        try {
            TenantContext.setTenantId(tenantId);
            tenantUserInitializerService.createTenantUser(tenantId, user);
        } finally {
            // Keep existing tenantId context active for request lifetime
            TenantContext.setTenantId(tenantId);
        }
    }
}

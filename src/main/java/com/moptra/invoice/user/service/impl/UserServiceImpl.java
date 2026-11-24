package com.moptra.invoice.user.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.comman.Constant.Constants;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.resolver.TenantResolver;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.dto.res.UserResponse;
import com.moptra.invoice.user.entities.rd.Employee;
import com.moptra.invoice.user.entities.rd.User;
import com.moptra.invoice.user.repository.EmployeeRepository;
import com.moptra.invoice.user.repository.UserRepository;
import com.moptra.invoice.user.service.MasterUserIdentityService;
import com.moptra.invoice.user.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Provisions internal Vendor users and Employee profiles in the Tenant DB
 * along with their TENANT login identity in the Master DB.
 * <p>
 * FUTURE CHANGE POINT:
 * - Validate email domain against vendor domain policy and resolve reportManager /
 * teamLead Employee references if IDs are passed in CreateEmployeeRequest.
 */
@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User, String> implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final MasterUserIdentityService masterUserIdentityService;
    private final TenantUserInitializerService tenantUserInitializerService;
    private final TenantResolver tenantResolver;

    public UserServiceImpl(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            MasterUserIdentityService masterUserIdentityService,
            TenantUserInitializerService tenantUserInitializerService, TenantResolver tenantResolver) {
        super(userRepository);
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.masterUserIdentityService = masterUserIdentityService;
        this.tenantUserInitializerService = tenantUserInitializerService;
        this.tenantResolver = tenantResolver;
    }

    @Override
    public UserResponse createUser(@NonNull CreateEmployeeRequest request) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null || currentUser.getCompanyId() == null) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "User details not found");
        }

        String tenantId = tenantResolver.resolveActiveTenantId(currentUser);

        // 1. Validate uniqueness in Tenant DB before creating Master DB identity
        TenantContext.setTenantId(tenantId);
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "User already exists in tenant with email: " + request.getEmail());
        }
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId().trim())) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Employee already exists with employeeId: " + request.getEmployeeId());
        }
        // 2. Create TENANT login identity in Master DB
        try {
            TenantContext.setTenantId(Constants.MASTER_TENANT);
            masterUserIdentityService.createMasterUserIdentity(currentUser.getCompanyId(), request);
        } finally {
            TenantContext.setTenantId(tenantId);
        }

        // 3. Create User + Employee in Tenant DB
        Employee employee = GenericMapper.map(request, Employee.class);
        employee.setEmployeeId(request.getEmployeeId().trim().toUpperCase());
        User user = GenericMapper.map(request, User.class);
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setEmployee(employee);
        employee.setUser(user);

        TenantContext.setTenantId(tenantId);
        tenantUserInitializerService.createTenantUser(tenantId, user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElse(user);
        log.info("Created vendor user '{}' (employeeId='{}') in tenantId='{}'", savedUser.getEmail(), employee.getEmployeeId(), tenantId);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
//                savedUser.getRole(),
                employee.getEmployeeId(),
                employee.getDesignation(),
                employee.getDepartment(),
                employee.getBranch(),
                employee.getJoiningDate()
        );
    }
}
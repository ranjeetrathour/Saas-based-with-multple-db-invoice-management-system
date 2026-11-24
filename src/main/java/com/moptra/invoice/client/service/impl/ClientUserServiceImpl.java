package com.moptra.invoice.client.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.client.repository.ClientCompanyRepository;
import com.moptra.invoice.client.service.ClientUserService;
import com.moptra.invoice.comman.Constant.Constants;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.resolver.TenantResolver;
import com.moptra.invoice.user.dto.req.CreateClientUserRequest;
import com.moptra.invoice.user.dto.res.ClientUserResponse;
import com.moptra.invoice.user.entities.rd.User;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.repository.UserRepository;
import com.moptra.invoice.user.service.MasterUserIdentityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Creates restricted Client Portal users (e.g., Petco users) without creating a vendor
 * Employee record:
 * 1) Validates ClientCompany exists in the active Tenant DB.
 * 2) Creates Master DB UserIdentity with IdentityType.CLIENT and clientCompanyId.
 * 3) Creates lightweight Tenant DB User (employee = null) for audit/approval references.
 *
 * FUTURE CHANGE POINT:
 * - Allow CLIENT_ADMIN of the same clientCompanyId (in addition to vendor COMPANY_ADMIN)
 *   to invite/create users strictly within their own clientCompanyId.
 */
@Service
@Slf4j
public class ClientUserServiceImpl extends BaseServiceImpl<User,String> implements ClientUserService {

    private final ClientCompanyRepository clientCompanyRepository;
    private final UserRepository userRepository;
    private final MasterUserIdentityService masterUserIdentityService;
    private final TenantResolver tenantResolver;

    public ClientUserServiceImpl(JpaRepository<User, String> repository, ClientCompanyRepository clientCompanyRepository, UserRepository userRepository, MasterUserIdentityService masterUserIdentityService, TenantResolver tenantResolver) {
        super(repository);
        this.clientCompanyRepository = clientCompanyRepository;
        this.userRepository = userRepository;
        this.masterUserIdentityService = masterUserIdentityService;
        this.tenantResolver = tenantResolver;
    }

    @Override
    public ClientUserResponse createClientUser(@NonNull String clientCompanyId, @NonNull CreateClientUserRequest request) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser==null || currentUser.getCompanyId()==null){
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "User details not found");
        }
        String tenantId = tenantResolver.resolveActiveTenantId(currentUser);
        String normalizedEmail = request.email().trim().toLowerCase();

        // 1. Verify ClientCompany exists and email is not already used in Tenant DB
        TenantContext.setTenantId(tenantId);
        if (!clientCompanyRepository.existsById(clientCompanyId)) {
            throw new GenericException(HttpStatus.NOT_FOUND.value(), "Client company not found with id: " + clientCompanyId);
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new GenericException(HttpStatus.CONFLICT.value(),"User already exists in tenant with email: " + normalizedEmail);
        }
        // 2. Create restricted CLIENT UserIdentity in Master DB
        UserIdentity savedIdentity;
        try {
            TenantContext.setTenantId(Constants.MASTER_TENANT);
            savedIdentity = masterUserIdentityService.createClientUserIdentity(currentUser.getCompanyId(), clientCompanyId, request);
        } finally {
            TenantContext.setTenantId(tenantId);
        }

        // 3. Create lightweight User (employee = null) in Tenant DB
        TenantContext.setTenantId(tenantId);
        User tenantClientUser = User.builder()
                .name(request.name().trim())
                .email(normalizedEmail)
                .role(request.role())
                .employee(null)
                .build();
        User savedTenantUser = userRepository.save(tenantClientUser);

        log.info("Created restricted CLIENT user email='{}' role='{}' under clientCompanyId='{}' in tenantId='{}'", normalizedEmail/* request.role()*/, clientCompanyId, tenantId);

        return new ClientUserResponse(
                savedIdentity.getId(),
                savedTenantUser.getId(),
                savedIdentity.getCompany() != null ? savedIdentity.getCompany().getId() : null,
                savedIdentity.getClientCompanyId(),
                savedIdentity.getName(),
                savedIdentity.getEmail(),
                savedIdentity.getIdentityType(),
                request.role(),
                savedIdentity.getStatus(),
                LocalDateTime.now()
        );
    }
}
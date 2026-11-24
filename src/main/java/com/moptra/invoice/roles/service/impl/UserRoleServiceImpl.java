package com.moptra.invoice.roles.service.impl;

import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.roles.dto.req.UserRoleReq;
import com.moptra.invoice.roles.dto.res.UserRoleRes;
import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.roles.repository.UserRoleRepository;
import com.moptra.invoice.roles.service.UserRoleService;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.resolver.TenantResolver;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manages tenant-scoped custom roles inside the active Tenant DB.
 *
 * FUTURE CHANGE POINT:
 * - Attach/sync permissions to roles once Permission and role_permissions are implemented.
 */
@Service
@Slf4j
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole, String> implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final TenantResolver tenantResolver;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, TenantResolver tenantResolver) {
        super(userRoleRepository);
        this.userRoleRepository = userRoleRepository;
        this.tenantResolver = tenantResolver;
    }

    @Override
    @Transactional
    public UserRoleRes createRole(@NonNull UserRoleReq request) {
        validateTenantContext();
        String normalizedName = request.name().trim().toUpperCase();

        if (userRoleRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Role already exists with name: " + normalizedName);
        }

        UserRole role = UserRole.builder()
                .name(normalizedName)
                .description(request.description() != null ? request.description().trim() : null)
                .build();

        UserRole saved = save(role);
        log.info("Created tenant role id='{}' name='{}' in tenant='{}'", saved.getId(), saved.getName(), TenantContext.getTenantId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleRes getRoleById(@NonNull String id) {
        validateTenantContext();
        UserRole role = userRoleRepository.findById(id).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Role not found with id: " + id));
        return toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleRes> getAllRoles() {
        validateTenantContext();
        return userRoleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserRoleRes updateRole(@NonNull String id, @NonNull UserRoleReq request) {
        validateTenantContext();
        UserRole existing = userRoleRepository.findById(id).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Role not found with id: " + id));

        String normalizedName = request.name().trim().toUpperCase();
        if (userRoleRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Another role already exists with name: " + normalizedName);
        }

        existing.setName(normalizedName);
        existing.setDescription(request.description() != null ? request.description().trim() : null);

        UserRole updated = save(existing);
        log.info("Updated tenant role id='{}' name='{}' in tenant='{}'", updated.getId(), updated.getName(), TenantContext.getTenantId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteRole(@NonNull String id) {
        validateTenantContext();
        UserRole existing = userRoleRepository.findById(id).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Role not found with id: " + id));

        if (existing.getUsers() != null && !existing.getUsers().isEmpty()) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Cannot delete role '" + existing.getName() + "' because users are assigned to it");
        }
        userRoleRepository.delete(existing);
        log.info("Deleted tenant role id='{}' name='{}' in tenant='{}'", id, existing.getName(), TenantContext.getTenantId());
    }

    private void validateTenantContext() {
        String currentTenant = TenantContext.getTenantId();
        if (currentTenant == null || currentTenant.isBlank() || "master".equalsIgnoreCase(currentTenant.trim())) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Valid tenant context is required for role operations");
        }
    }

    private UserRoleRes toResponse(UserRole role) {
        return new UserRoleRes(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}
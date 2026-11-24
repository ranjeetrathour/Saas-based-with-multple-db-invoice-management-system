package com.moptra.invoice.tenants.resolver;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.comman.Constant.Constants;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.tenants.context.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TenantResolver {

    public String resolveActiveTenantId(CustomUserDetails currentUser) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank() || Constants.MASTER_TENANT.equalsIgnoreCase(tenantId.trim())) {
            tenantId = currentUser != null ? currentUser.getTenantId() : null;
        }
        if (tenantId == null || tenantId.isBlank() || Constants.MASTER_TENANT.equalsIgnoreCase(tenantId.trim())) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Valid tenant context is required");
        }
        return tenantId.trim();
    }
}
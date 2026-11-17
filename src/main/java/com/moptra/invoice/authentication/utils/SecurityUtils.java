package com.moptra.invoice.authentication.utils;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.comman.exception.GenericException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static CustomUserDetails getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access");
        }

        return (CustomUserDetails) auth.getPrincipal();
    }
}
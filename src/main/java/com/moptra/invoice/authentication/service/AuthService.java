package com.moptra.invoice.authentication.service;

import com.moptra.invoice.user.dto.req.LoginRequest;
import com.moptra.invoice.user.dto.res.LoginResponse;

/**
 * Handles authentication and JWT issuance against Master DB identities.
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);
}
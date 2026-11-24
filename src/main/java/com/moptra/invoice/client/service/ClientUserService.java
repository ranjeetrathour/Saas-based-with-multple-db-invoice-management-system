package com.moptra.invoice.client.service;

import com.moptra.invoice.user.dto.req.CreateClientUserRequest;
import com.moptra.invoice.user.dto.res.ClientUserResponse;

public interface ClientUserService {
    ClientUserResponse createClientUser(String clientCompanyId, CreateClientUserRequest request);
}
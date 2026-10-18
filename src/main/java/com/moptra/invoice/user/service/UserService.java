package com.moptra.invoice.user.service;

import com.moptra.invoice.user.dto.req.CreateUserRequest;

public interface UserService {
    void createUser(CreateUserRequest request);
}

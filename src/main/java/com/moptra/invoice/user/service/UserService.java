package com.moptra.invoice.user.service;

import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.dto.res.UserResponse;

public interface UserService {
    UserResponse createUser(CreateEmployeeRequest request);
}

package com.moptra.invoice.user.service;

import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;

public interface UserService {
    void createUser(CreateEmployeeRequest request);
}

package com.moptra.invoice.user.service;

import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;

public interface MasterUserIdentityService {
     void createMasterUserIdentity(
            String companyId,
            CreateEmployeeRequest request);
}

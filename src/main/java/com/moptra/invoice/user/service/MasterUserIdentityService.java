package com.moptra.invoice.user.service;

import com.moptra.invoice.client.entities.ClientCompany;
import com.moptra.invoice.user.dto.req.CreateClientUserRequest;
import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.entities.rd.UserIdentity;

public interface MasterUserIdentityService {
     void createMasterUserIdentity(String companyId, CreateEmployeeRequest request);
     UserIdentity createClientUserIdentity(String companyId, String clientCompanyId, CreateClientUserRequest request);
}

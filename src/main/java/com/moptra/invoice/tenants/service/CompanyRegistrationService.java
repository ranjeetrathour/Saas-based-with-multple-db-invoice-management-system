package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.BranchRequest;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.BranchResponse;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;
import com.moptra.invoice.tenants.dto.res.TenantDetailsResponse;
import com.moptra.invoice.tenants.entites.rd.Company;


public interface CompanyRegistrationService {
    RegisterCompanyResponse register(RegisterCompanyRequest request);
    BranchResponse addBranches(BranchRequest branchRequests, String companyId);
    TenantDetailsResponse getTenantDetails();

    Company getById(String companyId);
}

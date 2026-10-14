package com.moptra.invoice.tenants.service;


import com.moptra.invoice.tenants.dto.req.ClientCompanyRequest;
import com.moptra.invoice.tenants.dto.res.ClientCompanyResponse;

import java.util.List;

public interface ClientCompanyService {
    ClientCompanyResponse createClientCompany(ClientCompanyRequest request);
    ClientCompanyResponse getClientCompanyById(Long id);
    List<ClientCompanyResponse> getAllClientCompanies();
    ClientCompanyResponse updateClientCompany(Long id, ClientCompanyRequest request);
}
package com.moptra.invoice.client.service;


import com.moptra.invoice.client.dto.req.ClientCompanyRequest;
import com.moptra.invoice.client.dto.res.ClientCompanyResponse;

import java.util.List;

public interface ClientCompanyService {
    ClientCompanyResponse createClientCompany(ClientCompanyRequest request);
    ClientCompanyResponse getClientCompanyById(Long id);
    List<ClientCompanyResponse> getAllClientCompanies();
    ClientCompanyResponse updateClientCompany(Long id, ClientCompanyRequest request);
}
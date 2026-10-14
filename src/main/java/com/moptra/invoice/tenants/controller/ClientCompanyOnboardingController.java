package com.moptra.invoice.tenants.controller;

import com.moptra.invoice.tenants.dto.req.ClientCompanyRequest;
import com.moptra.invoice.tenants.dto.res.ClientCompanyResponse;
import com.moptra.invoice.tenants.service.ClientCompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/client/onboarding"))
@AllArgsConstructor
public class ClientCompanyOnboardingController {
    private final ClientCompanyService clientCompanyService;

    @PostMapping
    public ResponseEntity<ClientCompanyResponse> onboardClientCompany(@RequestBody ClientCompanyRequest clientCompanyRequest){
        ClientCompanyResponse clientCompany = clientCompanyService.createClientCompany(clientCompanyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientCompany);

    }
}

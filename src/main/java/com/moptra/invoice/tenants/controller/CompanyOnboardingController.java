package com.moptra.invoice.tenants.controller;

import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;
import com.moptra.invoice.tenants.service.CompanyRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class CompanyOnboardingController {

    private final CompanyRegistrationService companyRegistrationService;


    @PostMapping("/company")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {

        RegisterCompanyResponse response = companyRegistrationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
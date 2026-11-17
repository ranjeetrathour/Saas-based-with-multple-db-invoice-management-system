package com.moptra.invoice.tenants.controller;

import com.moptra.invoice.tenants.dto.req.BranchRequest;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.BranchResponse;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;
import com.moptra.invoice.tenants.dto.res.TenantDetailsResponse;
import com.moptra.invoice.tenants.service.CompanyRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/add-branch")
    public ResponseEntity<?> addBranch(@Valid @RequestBody BranchRequest branchRequest, @RequestParam() String companyId){
        BranchResponse response = companyRegistrationService.addBranches(branchRequest, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * this is called before adding branch api called
     * it need to called based on permission if logged-in user is admin
     * @return
     */
    @GetMapping("/details")
    public ResponseEntity<TenantDetailsResponse> getTenantsDetails() {

        TenantDetailsResponse response = companyRegistrationService.getTenantDetails();

        return ResponseEntity.ok(response);
    }
}
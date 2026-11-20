package com.moptra.invoice.tenants.controller;

import com.moptra.invoice.tenants.dto.req.BranchRequest;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.BranchResponse;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;
import com.moptra.invoice.tenants.dto.res.TenantDetailsResponse;
import com.moptra.invoice.tenants.service.CompanyRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for onboarding Main Tenant / Service Provider (Vendor) companies
 * (e.g., Shyftlab), managing vendor internal branches, and retrieving tenant profile details.
 *
 * FLOW CONTEXT:
 * 1. Main Tenant / Vendor (e.g., Shyftlab) registers first via POST /api/v1/onboarding/companies.
 * 2. Vendor admin views tenant details or adds additional internal branches.
 * 3. Subsequent modules onboard Client Companies (e.g., Petco), provision restricted client users,
 *    and support client-led projects, per-employee SOWs, timesheets, and billing.
 *
 * FUTURE CHANGE POINT:
 * - Add @PreAuthorize annotations (e.g., hasAuthority('BRANCH_CREATE') / hasRole('COMPANY_ADMIN'))
 *   once RBAC permission expressions are wired into Spring Security.
 */
@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@Tag(
        name = "Tenant & Vendor Onboarding",
        description = "APIs for registering main tenant (vendor) companies, managing branches, and viewing tenant details"
)
public class CompanyOnboardingController {

    private final CompanyRegistrationService companyRegistrationService;

    /**
     * Registers a new Main Tenant / Vendor company, provisions its relational and MongoDB
     * databases, creates initial branches, and sets up the primary COMPANY_ADMIN account.
     *
     * FUTURE CHANGE POINT: Enforce API key/admin guard if public self-registration is disabled
     * in favor of invite/super-admin onboarding.
     */
    @PostMapping("/companies")
    @Operation(summary = "Register main tenant (vendor) company", description = "Onboards a vendor/tenant company, creates its branches and initial admin user, and provisions tenant databases")
    @ApiResponses(value = {@ApiResponse(responseCode = "201",
                    description = "Company registered and provisioned successfully",
                    content = @Content(schema = @Schema(implementation = RegisterCompanyResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Company email, company code, or admin email already exists", content = @Content)
    })
    public ResponseEntity<RegisterCompanyResponse> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        RegisterCompanyResponse response = companyRegistrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Adds a new internal branch (with GSTIN and billing address) under an existing vendor company.
     *
     * FUTURE CHANGE POINT: Restrict via @PreAuthorize so only an authorized vendor admin
     * of the target company (or SAAS_ADMIN) can add branches.
     */
    @PostMapping("/companies/{companyId}/branches")
    @Operation(
            summary = "Add internal branch to vendor company",
            description = "Creates a new branch with GSTIN and billing address under the specified vendor company"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Branch created successfully",
                    content = @Content(schema = @Schema(implementation = BranchResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid branch details", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vendor company not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Branch code already exists", content = @Content)
    })
    public ResponseEntity<BranchResponse> addBranch(@Parameter(description = "ID of the vendor company", required = true, example = "cmp_001") @PathVariable String companyId, @Valid @RequestBody BranchRequest branchRequest) {
        BranchResponse response = companyRegistrationService.addBranches(branchRequest, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves profile and branch details for the currently authenticated vendor/tenant.
     * Typically called before adding a branch or displaying tenant configuration in the dashboard.
     *
     * FUTURE CHANGE POINT: Restrict access to authenticated tenant/admin roles and block
     * restricted client portal users from calling vendor-level tenant metadata.
     */
    @GetMapping("/companies/current")
    @Operation(
            summary = "Get current vendor/tenant details",
            description = "Fetches company profile and internal branches for the currently authenticated vendor tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tenant details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TenantDetailsResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
    })
    public ResponseEntity<TenantDetailsResponse> getTenantsDetails() {
        TenantDetailsResponse response = companyRegistrationService.getTenantDetails();
        return ResponseEntity.ok(response);
    }
}
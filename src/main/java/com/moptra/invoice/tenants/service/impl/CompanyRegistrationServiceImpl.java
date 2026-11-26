package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.authentication.utils.SecurityUtils;
import com.moptra.invoice.comman.Constant.Constants;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.roles.entites.UserRole;
import com.moptra.invoice.roles.repository.UserRoleRepository;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.dto.req.BranchRequest;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.BranchResponse;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;
import com.moptra.invoice.tenants.dto.res.TenantDetailsResponse;
import com.moptra.invoice.tenants.entites.rd.Branch;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.entites.rd.TenantDatabase;
import com.moptra.invoice.tenants.enums.CompanyStatus;
import com.moptra.invoice.tenants.enums.TenantDatabaseStatus;
import com.moptra.invoice.tenants.provisioning.TenantDatabaseProvisioningService;
import com.moptra.invoice.tenants.provisioning.TenantMongoProvisioningService;
import com.moptra.invoice.tenants.repository.BranchRepository;
import com.moptra.invoice.tenants.repository.CompanyRepository;
import com.moptra.invoice.tenants.repository.TenantDatabaseRepository;
import com.moptra.invoice.tenants.service.CompanyRegistrationService;
import com.moptra.invoice.tenants.service.MasterCompanyRegistrationService;
import com.moptra.invoice.tenants.service.TenantConfigService;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.repository.UserIdentityRepository;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Handles Main Tenant / Service Provider (Vendor) registration (e.g., Shyftlab),
 * physical/logical tenant database + MongoDB provisioning, initial admin setup,
 * and vendor internal branch management[cite: 1, 2, 3].
 *
 * FLOW CONTEXT:
 * 1. Main Tenant (Vendor, e.g., Shyftlab) registers here first[cite: 1].
 * 2. After tenant is active, vendor creates internal staff (HR, Finance, Managers, Devs)
 *    and onboards Client Companies (e.g., Petco) with restricted Client Portal users[cite: 1, 2].
 * 3. Client creates Projects & per-employee SOWs; Vendor/Client allocates employees[cite: 1].
 *
 * FUTURE CHANGE POINT:
 * - Enforce strict RBAC/permission checks (e.g., SAAS_ADMIN / COMPANY_ADMIN) before onboarding/branching[cite: 3].
 * - Seed default vendor roles/departments/designations in the new tenant DB right after provisioning[cite: 4].
 */
@Slf4j
@Service
public class CompanyRegistrationServiceImpl extends BaseServiceImpl<Company, String> implements CompanyRegistrationService {

    private final UserIdentityRepository userIdentityRepository;
    private final TenantDatabaseRepository tenantDatabaseRepository;
    private final CompanyRepository companyRepository;
    private final TenantDatabaseProvisioningService tenantDatabaseProvisioningService;
    private final TenantConfigService tenantConfigService;
    private final TenantUserInitializerService tenantUserInitializerService;
    private final TenantMongoProvisioningService tenantMongoProvisioningService;
    private final BranchRepository branchRepository;
    private final MasterCompanyRegistrationService masterCompanyRegistrationService;
    private final UserRoleRepository userRoleRepository;

    public CompanyRegistrationServiceImpl(
            CompanyRepository companyRepository,
            UserIdentityRepository userIdentityRepository,
            TenantDatabaseRepository tenantDatabaseRepository,
            TenantDatabaseProvisioningService tenantDatabaseProvisioningService,
            TenantConfigService tenantConfigService,
            TenantUserInitializerService tenantUserInitializerService,
            TenantMongoProvisioningService tenantMongoProvisioningService,
            BranchRepository branchRepository,
            MasterCompanyRegistrationService masterCompanyRegistrationService, UserRoleRepository userRoleRepository) {
        super(companyRepository);
        this.companyRepository = companyRepository;
        this.userIdentityRepository = userIdentityRepository;
        this.tenantDatabaseRepository = tenantDatabaseRepository;
        this.tenantDatabaseProvisioningService = tenantDatabaseProvisioningService;
        this.tenantConfigService = tenantConfigService;
        this.tenantUserInitializerService = tenantUserInitializerService;
        this.tenantMongoProvisioningService = tenantMongoProvisioningService;
        this.branchRepository = branchRepository;
        this.masterCompanyRegistrationService = masterCompanyRegistrationService;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Registers a new Main Tenant / Vendor company in Master DB, provisions its relational
     * and MongoDB databases, creates login UserIdentity + initial Tenant Admin, and marks
     * the company ACTIVE[cite: 1, 2, 3].
     *
     * FUTURE CHANGE POINT: Add domain-level duplicate checks and seed default RBAC roles
     * (HR, FINANCE, MANAGER, EMPLOYEE) inside the newly provisioned tenant database[cite: 3, 4].
     */
    @Override
    public RegisterCompanyResponse register(RegisterCompanyRequest request) {
        validateRequest(request);

        // 1. Persist Main Tenant / Vendor company record in Master DB
        Company companySavedInMasterDb = masterCompanyRegistrationService.createCompany(request);
        log.info("Vendor/Tenant company '{}' registered in Master DB with id={}", companySavedInMasterDb.getCompanyName(), companySavedInMasterDb.getId());

        String sanitizedCompanyCode = companySavedInMasterDb.getCompanyCode()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "_");
        String databaseName = "tenant_" + sanitizedCompanyCode;

        TenantDatabase tenantDatabase = masterCompanyRegistrationService.registerTenantDbDetails(companySavedInMasterDb, databaseName);

        try {
            // 2. Provision relational DB, run migrations, register DataSource, and init MongoDB
            tenantDatabaseProvisioningService.createDatabase(databaseName);
            log.info("db created for the company {} ", request.companyName());
            tenantConfigService.registerNewTenant(tenantDatabase);
            tenantMongoProvisioningService.createDataBase(databaseName);
            log.info("Relational and MongoDB databases provisioned for tenant DB '{}'", databaseName);
// Create initial Tenant Admin inside the tenant DB
            String previousTenantId = TenantContext.getTenantId();
            // 3. Save login identity in Master DB (scoping to Vendor tenant)
            UserRole userRole;
                try {
                    TenantContext.setTenantId(databaseName);
                    userRole = userRoleRepository.fetchCompanyAdminRoleData().orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Role not found: COMPANY_ADMIN"));
                }finally {
                    if (previousTenantId != null) {
                        TenantContext.setTenantId(previousTenantId);
                    } else {
                        TenantContext.setTenantId(Constants.MASTER_TENANT);
                    }
                    log.info("Restored TenantContext to '{}'", TenantContext.getTenantId());
                }
            UserIdentity userIdentityInMasterDbForLogin = masterCompanyRegistrationService.createUserIdentityInMasterDbForLogin(userRole, companySavedInMasterDb, request);
            log.info("Master login UserIdentity saved for email '{}'", userIdentityInMasterDbForLogin.getEmail());

            previousTenantId = TenantContext.getTenantId();
            try {
                TenantContext.setTenantId(databaseName);
                log.info("Switched TenantContext to '{}' for initial admin creation", databaseName);
                tenantUserInitializerService.createTenantAdminUser(databaseName, request);
            } finally {
                if (previousTenantId != null) {
                    TenantContext.setTenantId(previousTenantId);
                } else {
                    TenantContext.setTenantId("master");
                }
                log.info("Restored TenantContext to '{}'", TenantContext.getTenantId());
            }

            // 5. Activate TenantDatabase and Company statuses in Master DB
            tenantDatabase.setStatus(TenantDatabaseStatus.ACTIVE);
            tenantDatabaseRepository.save(tenantDatabase);

            companySavedInMasterDb.setStatus(CompanyStatus.ACTIVE);
            save(companySavedInMasterDb);

            return RegisterCompanyResponse.builder()
                    .companyId(companySavedInMasterDb.getId())
                    .userId(userIdentityInMasterDbForLogin.getId())
                    .companyName(companySavedInMasterDb.getCompanyName())
                    .adminEmail(userIdentityInMasterDbForLogin.getEmail())
                    .status(companySavedInMasterDb.getStatus().name())
                    .message("Company registered successfully.")
                    .build();

        } catch (Exception exception) {
            log.info("Error during company registration for '{}': ", request.companyName(), exception);
            masterCompanyRegistrationService.markProvisioningFailed(companySavedInMasterDb.getId(), tenantDatabase.getId());
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Company registration failed: " + exception.getMessage());
        }
    }

    /**
     * Adds a new internal branch to a Main Tenant / Vendor company[cite: 1, 3].
     *
     * FUTURE CHANGE POINT: Enforce that only an authorized COMPANY_ADMIN of the matching
     * company (or SAAS_ADMIN) can add branches, and ensure tenant/master DB routing is explicit[cite: 3].
     */
    @Override
    @Transactional
    public BranchResponse addBranches(@Nonnull BranchRequest branchRequest, String companyId) {
        if (branchRepository.findByBranchCode(branchRequest.branchCode())) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Branch code already exists: " + branchRequest.branchCode());
        }

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Company not found"));

        Branch branch = GenericMapper.map(branchRequest, Branch.class);
        branch.setCompany(company);

        Branch savedBranch = branchRepository.save(branch);

        return new BranchResponse(
                savedBranch.getId(),
                savedBranch.getBranchCode(),
                savedBranch.getBranchAddress(),
                savedBranch.getBranchCity(),
                savedBranch.getBranchCountry()
        );
    }

    /**
     * Fetches profile and internal branches of the currently authenticated Main Tenant / Vendor company[cite: 1].
     *
     * FUTURE CHANGE POINT: Restrict client portal users from calling vendor-level tenant details
     * if they should only access client portal APIs.
     */
    @Override
    public TenantDetailsResponse getTenantDetails() {
        CustomUserDetails user = SecurityUtils.getCurrentUser();
        Company company;
        List<Branch> branches;

        try {
            TenantContext.setTenantId("master");
            company = companyRepository.findById(user.getCompanyId()).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Company not found"));
            branches = branchRepository.findByCompanyId(company.getId());
        } finally {
            TenantContext.clear();
        }

        List<BranchResponse> branchResponses = branches.stream()
                .map(branch -> new BranchResponse(
                        branch.getId(),
                        branch.getBranchCode(),
                        branch.getBranchAddress(),
                        branch.getBranchCity(),
                        branch.getBranchCountry()
                ))
                .toList();

        return new TenantDetailsResponse(
                company.getId(),
                company.getCompanyName(),
                company.getCompanyCode(),
                company.getCompanyEmail(),
                company.getCompanyPhone(),
                company.getWebsite(),
                company.getStatus().name(),
                branchResponses
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Company getById(String companyId) {
        return (findById(companyId));
    }

    /**
     * Validates duplicate company email, company code, and initial admin email before provisioning.
     *
     * FUTURE CHANGE POINT: Update email uniqueness check if composite uniqueness
     * (company + clientCompany + email) is enforced for client portal users.
     */
    private void validateRequest(@Nonnull RegisterCompanyRequest request) {
        if (companyRepository.existsByCompanyEmail(request.companyEmail())) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Company email already exists");
        }
        if (companyRepository.existsByCompanyCode(request.companyCode())) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Company code already exists");
        }
        if (userIdentityRepository.existsByEmail(request.adminEmail())) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "Admin email already exists");
        }
    }
}
package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.authentication.utils.SecurityUtils;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
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
import com.moptra.invoice.tenants.service.*;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.repository.UserIdentityRepository;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public CompanyRegistrationServiceImpl(
            CompanyRepository companyRepository,
            UserIdentityRepository userIdentityRepository,
            TenantDatabaseRepository tenantDatabaseRepository,
            TenantDatabaseProvisioningService tenantDatabaseProvisioningService,
            TenantConfigService tenantConfigService,
            TenantUserInitializerService tenantUserInitializerService,
            TenantMongoProvisioningService tenantMongoProvisioningService,  BranchRepository branchRepository, MasterCompanyRegistrationService masterCompanyRegistrationService) {
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
    }

    /**
     * need to add some validation like not all person can onboard tenant
     * and only tenant admin can add or onboard new branch
     * @param request
     * @return
     */
    @Override
    @Transactional
    public RegisterCompanyResponse register(RegisterCompanyRequest request) {

        /**
         * later this must be checked by company domain
         */
        if (companyRepository.findByCompanyName(request.getCompanyName())){
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Company Already Register With This Domain You Can Add New Branch");
        }

        validateRequest(request);

        Company companySavedInMasterDb = masterCompanyRegistrationService.createCompany(request);

        log.info("Company registered successfully in Master DB");
        String sanitizedCompanyName = companySavedInMasterDb.getCompanyCode().toLowerCase().replaceAll("[^a-z0-9]", "_");
        String databaseName = "tenant_" + sanitizedCompanyName;
        TenantDatabase tenantDatabase = masterCompanyRegistrationService.registerTenantDbDetails(companySavedInMasterDb, databaseName);

        try {
            // 2. Create dynamic physical database (later it take complete request so it can create database on the user server)
            tenantDatabaseProvisioningService.createDatabase(databaseName);
            // 3. Register tenant DataSource & execute Flyway migrations
            tenantConfigService.registerNewTenant(tenantDatabase);
            tenantMongoProvisioningService.createDataBase(databaseName);

            log.info("db created and migration run success ");
            // 4. Save UserIdentity in master db for login
            UserIdentity userIdentityInMasterDbForLogin = masterCompanyRegistrationService.createUserIdentityInMasterDbForLogin(companySavedInMasterDb, request);

            log.info("User identity saved in Master DB: {}", userIdentityInMasterDbForLogin.getEmail());

            // 5. Save Tenant User (Context set before invoking REQUIRES_NEW transaction)
            try {
                TenantContext.setTenantId(databaseName);
                log.info("Tenant context set to {} prior to transaction start", TenantContext.getTenantId());
                tenantUserInitializerService.createTenantAdminUser(databaseName, request);
            } finally {
                TenantContext.setTenantId("master");
                log.info("Current DB context restored to: {}", TenantContext.getTenantId());
            }

            // 6. Update Statuses in Master DB
            tenantDatabase.setStatus(TenantDatabaseStatus.ACTIVE);
            tenantDatabaseRepository.save(tenantDatabase);

            companySavedInMasterDb.setStatus(CompanyStatus.ACTIVE);
            save(companySavedInMasterDb);

            return RegisterCompanyResponse.builder()
                    .companyName(companySavedInMasterDb.getCompanyName())
                    .adminEmail(userIdentityInMasterDbForLogin.getEmail())
                    .status(companySavedInMasterDb.getStatus().name())
                    .message("Company registered successfully.")
                    .build();

        } catch (Exception exception) {
            log.error("Error during company registration: ", exception);
            masterCompanyRegistrationService.markProvisioningFailed(companySavedInMasterDb.getId(), tenantDatabase.getId());
            throw new RuntimeException("Company registration failed: " + exception.getMessage(), exception);
        }
    }

    /**
     * need to check permission first
     */
    @Override
    public BranchResponse addBranches(@Nonnull BranchRequest branchRequest, String id) {

        if (branchRepository.findByBranchCode(branchRequest.getBranchCode())) {
            throw new RuntimeException("Branch code already exists");
        }

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(),"Company not found"));

        Branch branch = GenericMapper.map(branchRequest, Branch.class);

        branch.setCompany(company);

        Branch savedBranch = branchRepository.save(branch);

        return GenericMapper.map(savedBranch, BranchResponse.class);
    }

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

    private void validateRequest(@Nonnull RegisterCompanyRequest request) {
        if (companyRepository.existsByCompanyEmail(request.getCompanyEmail())) {
            throw new IllegalArgumentException("Company already exists");
        }
        if (userIdentityRepository.existsByEmail(request.getAdminEmail())) {
            throw new IllegalArgumentException("Admin email already exists");
        }
    }
}
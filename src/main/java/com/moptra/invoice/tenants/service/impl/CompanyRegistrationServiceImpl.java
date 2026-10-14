package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;
import com.moptra.invoice.tenants.entites.rd.Branch;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.entites.rd.TenantDatabase;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.tenants.enums.CompanyStatus;
import com.moptra.invoice.tenants.enums.TenantDatabaseStatus;
import com.moptra.invoice.tenants.provisioning.TenantDatabaseProvisioningService;
import com.moptra.invoice.tenants.provisioning.TenantMongoProvisioningService;
import com.moptra.invoice.tenants.repository.rd.CompanyRepository;
import com.moptra.invoice.tenants.repository.rd.TenantDatabaseRepository;
import com.moptra.invoice.tenants.service.BranchService;
import com.moptra.invoice.tenants.service.CompanyRegistrationService;
import com.moptra.invoice.tenants.service.TenantConfigService;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.enums.IdentityType;
import com.moptra.invoice.user.enums.UserRole;
import com.moptra.invoice.user.repository.rd.UserIdentityRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
public class CompanyRegistrationServiceImpl extends BaseServiceImpl<Company, String> implements CompanyRegistrationService {

    private final UserIdentityRepository userIdentityRepository;
    private final TenantDatabaseRepository tenantDatabaseRepository;
    private final CompanyRepository companyRepository;
    private final TenantDatabaseProvisioningService tenantDatabaseProvisioningService;
    private final TenantConfigService tenantConfigService;
    private final TenantUserInitializerService tenantUserInitializerService;
    private final PasswordEncoder passwordEncoder;
    private final TenantMongoProvisioningService tenantMongoProvisioningService;
    private final BranchService branchService;

    public CompanyRegistrationServiceImpl(
            CompanyRepository companyRepository,
            UserIdentityRepository userIdentityRepository,
            TenantDatabaseRepository tenantDatabaseRepository,
            TenantDatabaseProvisioningService tenantDatabaseProvisioningService,
            TenantConfigService tenantConfigService,
            TenantUserInitializerService tenantUserInitializerService,
            PasswordEncoder passwordEncoder, TenantMongoProvisioningService tenantMongoProvisioningService, BranchService branchService) {
        super(companyRepository);
        this.companyRepository = companyRepository;
        this.userIdentityRepository = userIdentityRepository;
        this.tenantDatabaseRepository = tenantDatabaseRepository;
        this.tenantDatabaseProvisioningService = tenantDatabaseProvisioningService;
        this.tenantConfigService = tenantConfigService;
        this.tenantUserInitializerService = tenantUserInitializerService;
        this.passwordEncoder = passwordEncoder;
        this.tenantMongoProvisioningService = tenantMongoProvisioningService;
        this.branchService = branchService;
    }

    @Override
    @Transactional
    public RegisterCompanyResponse register(RegisterCompanyRequest request) {
        validateRequest(request);

        // 1. Save Company In Master DB
        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .companyEmail(request.getCompanyEmail())
                .companyPhone(request.getCompanyPhone())
                .status(CompanyStatus.PROVISIONING)
                .build();
        company = save(company);

        // 2. Map and Save Branches associated with the Company
        final Company savedCompany = company;
        var branches = request.getBranches().stream()
                .map(branchReq -> Branch.builder()
                        .company(savedCompany)
                        .branchName(branchReq.getBranchName())
                        .branchCode(branchReq.getBranchCode())
                        .gstNo(branchReq.getGstNo())
                        .branchAddress(branchReq.getBranchAddress())
                        .branchCity(branchReq.getBranchCity())
                        .branchCountry(branchReq.getBranchCountry())
                        .isActive(true)
                        .build())
                .collect(Collectors.toList());

        branchService.saveBranches(branches);
        log.info("Company and its {} branches registered successfully in Master DB", branches.size());

        log.info("Company registered successfully in Master DB");
        String sanitizedCompanyName = company.getCompanyCode().toLowerCase().replaceAll("[^a-z0-9]", "_");
        String databaseName = "tenant_" + sanitizedCompanyName;

        TenantDatabase tenantDatabase = TenantDatabase.builder()
                .company(company)
                .databaseName(databaseName)
                .host("localhost")
                .port(5432)
                .username("postgres")
                .password("postgres")
                .status(TenantDatabaseStatus.PROVISIONING)
                .build();
        tenantDatabase = tenantDatabaseRepository.save(tenantDatabase);
        log.info("Tenant DB metadata details saved in Master DB");

        try {
            // 2. Create dynamic physical database (later it take complete request so it can create database on the user server)
            tenantDatabaseProvisioningService.createDatabase(databaseName);
            // 3. Register tenant DataSource & execute Flyway migrations
            tenantConfigService.registerNewTenant(tenantDatabase);
            tenantMongoProvisioningService.createDataBase(databaseName);

            log.info("db created and migration run success ");
            // 4. Save UserIdentity in master db for login
            UserIdentity identity = UserIdentity.builder()
                    .company(company)
                    .name(request.getAdminName())
                    .email(request.getAdminEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(UserRole.COMPANY_ADMIN)
                    .status(AccountStatus.ACTIVE)
                    .identityType(IdentityType.TENANT)
                    .build();
            identity = userIdentityRepository.save(identity);

            log.info("User identity saved in Master DB: {}", identity.getEmail());

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

            company.setStatus(CompanyStatus.ACTIVE);
            save(company);

            return RegisterCompanyResponse.builder()
                    .companyName(company.getCompanyName())
                    .adminEmail(identity.getEmail())
                    .status(company.getStatus().name())
                    .message("Company registered successfully.")
                    .build();

        } catch (Exception exception) {
            log.error("Error during company registration: ", exception);
            company.setStatus(CompanyStatus.PROVISIONING_FAILED);
            save(company);
            tenantDatabase.setStatus(TenantDatabaseStatus.FAILED);
            tenantDatabaseRepository.save(tenantDatabase);
            throw new RuntimeException("Company registration failed: " + exception.getMessage(), exception);
        }
    }

    private void validateRequest(RegisterCompanyRequest request) {
        if (companyRepository.existsByCompanyEmail(request.getCompanyEmail())) {
            throw new IllegalArgumentException("Company email already exists");
        }
        if (userIdentityRepository.existsByEmail(request.getAdminEmail())) {
            throw new IllegalArgumentException("Admin email already exists");
        }
    }
}
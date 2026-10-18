package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.dto.res.RegisterCompanyResponse;
import com.moptra.invoice.tenants.entites.Company;
import com.moptra.invoice.tenants.entites.TenantDatabase;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.tenants.enums.CompanyStatus;
import com.moptra.invoice.tenants.enums.TenantDatabaseStatus;
import com.moptra.invoice.tenants.provisioning.TenantDatabaseProvisioningService;
import com.moptra.invoice.tenants.repository.rd.CompanyRepository;
import com.moptra.invoice.tenants.repository.rd.TenantDatabaseRepository;
import com.moptra.invoice.tenants.service.CompanyRegistrationService;
import com.moptra.invoice.tenants.service.TenantConfigService;
import com.moptra.invoice.tenants.service.TenantUserInitializerService;
import com.moptra.invoice.user.entities.UserIdentity;
import com.moptra.invoice.user.enums.IdentityType;
import com.moptra.invoice.user.enums.UserRole;
import com.moptra.invoice.user.repository.rd.UserIdentityRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CompanyRegistrationServiceImpl(
            CompanyRepository companyRepository,
            UserIdentityRepository userIdentityRepository,
            TenantDatabaseRepository tenantDatabaseRepository,
            TenantDatabaseProvisioningService tenantDatabaseProvisioningService,
            TenantConfigService tenantConfigService,
            TenantUserInitializerService tenantUserInitializerService,
            PasswordEncoder passwordEncoder) {
        super(companyRepository);
        this.companyRepository = companyRepository;
        this.userIdentityRepository = userIdentityRepository;
        this.tenantDatabaseRepository = tenantDatabaseRepository;
        this.tenantDatabaseProvisioningService = tenantDatabaseProvisioningService;
        this.tenantConfigService = tenantConfigService;
        this.tenantUserInitializerService = tenantUserInitializerService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegisterCompanyResponse register(RegisterCompanyRequest request) {
        validateRequest(request);

        // 1. Save Master Entities
        Company company = Company.builder()
                .name(request.getCompanyName())
                .code("comp-" + request.getCompanyName())
                .email(request.getCompanyEmail())
                .phone(request.getPhone())
                .status(CompanyStatus.PROVISIONING)
                .build();
        company = save(company);

        log.info("Company registered successfully in Master DB");
        String sanitizedCompanyName = company.getCode().toLowerCase().replaceAll("[^a-z0-9]", "_");
        String databaseName = "tenant_" + sanitizedCompanyName;

        // in future have to take username and password, host etc from user as well
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

            // 4. Save Master UserIdentity
            UserIdentity identity = UserIdentity.builder()
                    .company(company)
                    .name(request.getAdminName())
                    .email(request.getAdminEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(UserRole.COMPANY_ADMIN)
                    .status(AccountStatus.ACTIVE)
                    .identityType(IdentityType.PLATFORM)
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
                    .companyName(company.getName())
                    .adminEmail(identity.getEmail())
                    .databaseName(databaseName)
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
        if (companyRepository.existsByEmail(request.getCompanyEmail())) {
            throw new IllegalArgumentException("Company email already exists");
        }
        if (userIdentityRepository.existsByEmail(request.getAdminEmail())) {
            throw new IllegalArgumentException("Admin email already exists");
        }
    }
}
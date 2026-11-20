package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.dto.req.RegisterCompanyRequest;
import com.moptra.invoice.tenants.entites.rd.Branch;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.entites.rd.TenantDatabase;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.tenants.enums.CompanyStatus;
import com.moptra.invoice.tenants.enums.TenantDatabaseStatus;
import com.moptra.invoice.tenants.repository.CompanyRepository;
import com.moptra.invoice.tenants.repository.TenantDatabaseRepository;
import com.moptra.invoice.tenants.service.BranchService;
import com.moptra.invoice.tenants.service.MasterCompanyRegistrationService;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.enums.IdentityType;
import com.moptra.invoice.user.enums.UserRole;
import com.moptra.invoice.user.repository.UserIdentityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Persists core Main Tenant / Service Provider (Vendor) records (e.g., Shyftlab)
 * in the Master Database: Company, initial internal Branches, TenantDatabase metadata,
 * and initial login UserIdentity.
 *
 * FLOW CONTEXT:
 * - Main Tenant (Vendor, e.g., Shyftlab) is onboarded first into Master DB.
 * - After tenant setup, vendor creates internal staff (HR, Finance, Managers, Devs)
 *   and onboards Client Companies (e.g., Petco) with restricted Client Portal users.
 * - Client creates Projects & per-employee SOWs; employees are allocated and log timesheets
 *   which flow through approval, vendor finance billing, and client ACK/NACK.
 *
 * FUTURE CHANGE POINT:
 * - Externalize tenant DB host/port/credentials via configuration or encrypted vault/BYO-DB input.
 * - Add helper/hook to provision initial restricted CLIENT_ADMIN login when a ClientCompany is onboarded.
 */
@Service
@Slf4j
@Tag(
        name = "Master Tenant Provisioning Service",
        description = "Internal service layer for registering vendor/tenant companies, initial branches, DB metadata, and admin identity"
)
public class MasterCompanyRegistrationServiceImpl extends BaseServiceImpl<Company, String> implements MasterCompanyRegistrationService {

    private final CompanyRepository companyRepository;
    private final BranchService branchService;
    private final TenantDatabaseRepository tenantDatabaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserIdentityRepository userIdentityRepository;

    public MasterCompanyRegistrationServiceImpl(
            JpaRepository<Company, String> repository,
            CompanyRepository companyRepository,
            BranchService branchService,
            TenantDatabaseRepository tenantDatabaseRepository,
            PasswordEncoder passwordEncoder,
            UserIdentityRepository userIdentityRepository) {
        super(repository);
        this.companyRepository = companyRepository;
        this.branchService = branchService;
        this.tenantDatabaseRepository = tenantDatabaseRepository;
        this.passwordEncoder = passwordEncoder;
        this.userIdentityRepository = userIdentityRepository;
    }

    /**
     * Creates the Main Tenant / Vendor company and its initial internal branches
     * in the Master DB with status PROVISIONING.
     *
     * FUTURE CHANGE POINT: Support optional client-company onboarding under this vendor
     * in the client module without mixing client records into main Company rows.
     */
    @Override
    @Transactional
    @Operation(
            summary = "Create vendor company and initial branches in Master DB",
            description = "Persists the main tenant (vendor) company with PROVISIONING status along with its initial branches"
    )
    public Company createCompany(@Nonnull RegisterCompanyRequest request) {
        Company company = Company.builder()
                .companyName(request.companyName())
                .companyCode(request.companyCode())
                .companyEmail(request.companyEmail())
                .companyPhone(request.companyPhone())
                .website(request.website())
                .status(CompanyStatus.PROVISIONING)
                .build();

        final Company savedCompany = save(company);

        List<Branch> branches = request.branches().stream()
                .map(branch -> Branch.builder()
                        .company(savedCompany)
                        .branchName(branch.branchName())
                        .branchCode(branch.branchCode())
                        .gstNo(branch.gstNo())
                        .branchAddress(branch.branchAddress())
                        .branchCity(branch.branchCity())
                        .branchCountry(branch.branchCountry())
                        .isActive(true)
                        .build())
                .collect(Collectors.toList());

        branchService.saveBranches(branches);
        log.info("Vendor company '{}' and {} branches saved in Master DB",
                savedCompany.getCompanyName(), branches.size());

        return savedCompany;
    }

    /**
     * Marks both the vendor Company and TenantDatabase records as failed in a separate
     * transaction when provisioning encounters an error.
     *
     * FUTURE CHANGE POINT: Capture failure reason/timestamp if audit diagnostics are needed.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Operation(
            summary = "Mark tenant provisioning as failed",
            description = "Updates company status to PROVISIONING_FAILED and tenant DB status to FAILED in a new transaction"
    )
    public void markProvisioningFailed(String companyId, String tenantDatabaseId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
        TenantDatabase tenantDatabase = tenantDatabaseRepository.findById(tenantDatabaseId)
                .orElseThrow(() -> new IllegalArgumentException("TenantDatabase not found with id: " + tenantDatabaseId));

        company.setStatus(CompanyStatus.PROVISIONING_FAILED);
        tenantDatabase.setStatus(TenantDatabaseStatus.FAILED);

        companyRepository.save(company);
        tenantDatabaseRepository.save(tenantDatabase);
        log.warn("Marked provisioning failed for companyId={} and tenantDatabaseId={}",
                companyId, tenantDatabaseId);
    }

    /**
     * Registers tenant database connection/routing metadata in Master DB with status PROVISIONING.
     *
     * FUTURE CHANGE POINT: Load host/port/username/password from app properties or per-tenant
     * custom DB config instead of hardcoded defaults, and encrypt credentials.
     */
    @Override
    @Transactional
    @Operation(
            summary = "Register tenant database metadata",
            description = "Saves database routing and connection metadata for the vendor tenant in Master DB"
    )
    public TenantDatabase registerTenantDbDetails(@Nonnull Company company, String dbName) {
        TenantDatabase tenantDatabase = TenantDatabase.builder()
                .company(company)
                .databaseName(dbName)
                .host("localhost")
                .port(5432)
                .username("postgres")
                .password("postgres")
                .status(TenantDatabaseStatus.PROVISIONING)
                .build();

        TenantDatabase savedDb = tenantDatabaseRepository.save(tenantDatabase);
        log.info("Saved TenantDatabase metadata '{}' for companyId={}", dbName, company.getId());
        return savedDb;
    }

    /**
     * Creates the initial COMPANY_ADMIN login identity for the Vendor/Tenant in Master DB.
     *
     * FUTURE CHANGE POINT: When creating restricted CLIENT_ADMIN / CLIENT_TL / CLIENT_FINANCE
     * users for a ClientCompany (e.g., Petco), set clientCompany on UserIdentity and
     * identityType = IdentityType.CLIENT to keep vendor and client users isolated.
     */
    @Override
    @Operation(
            summary = "Create initial vendor admin login identity",
            description = "Creates the primary COMPANY_ADMIN identity in Master DB for vendor login"
    )
    public UserIdentity createUserIdentityInMasterDbForLogin(
            Company company, @Nonnull RegisterCompanyRequest request) {
        UserIdentity user = UserIdentity.builder()
                .company(company)
                .name(request.adminName())
                .email(request.adminEmail())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.COMPANY_ADMIN)
                .status(AccountStatus.ACTIVE)
                .identityType(IdentityType.TENANT)
                .build();

        UserIdentity savedUser = userIdentityRepository.save(user);
        log.info("Created initial TENANT COMPANY_ADMIN identity for email='{}'", savedUser.getEmail());
        return savedUser;
    }
}
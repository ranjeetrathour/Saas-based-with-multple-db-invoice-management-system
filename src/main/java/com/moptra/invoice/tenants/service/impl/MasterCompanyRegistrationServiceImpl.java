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
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Slf4j
public class MasterCompanyRegistrationServiceImpl extends BaseServiceImpl<Company, String> implements MasterCompanyRegistrationService {

    private final CompanyRepository companyRepository;
    private final BranchService branchService;
    private final TenantDatabaseRepository tenantDatabaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserIdentityRepository userIdentityRepository;

    public MasterCompanyRegistrationServiceImpl(JpaRepository<Company, String> repository, CompanyRepository companyRepository, BranchService branchService, TenantDatabaseRepository tenantDatabaseRepository, PasswordEncoder passwordEncoder, UserIdentityRepository userIdentityRepository) {
        super(repository);
        this.companyRepository = companyRepository;
        this.branchService = branchService;
        this.tenantDatabaseRepository = tenantDatabaseRepository;
        this.passwordEncoder = passwordEncoder;
        this.userIdentityRepository = userIdentityRepository;
    }

    @Override
    @Transactional
    public Company createCompany(@Nonnull RegisterCompanyRequest request) {
        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .companyEmail(request.getCompanyEmail())
                .companyPhone(request.getCompanyPhone())
                .status(CompanyStatus.PROVISIONING)
                .build();

        company = save(company);

        final Company savedCompany = company;

        var branches = request.getBranches().stream()
                .map(branch -> Branch.builder()
                        .company(savedCompany)
                        .branchName(branch.getBranchName())
                        .branchCode(branch.getBranchCode())
                        .gstNo(branch.getGstNo())
                        .branchAddress(branch.getBranchAddress())
                        .branchCity(branch.getBranchCity())
                        .branchCountry(branch.getBranchCountry())
                        .isActive(true)
                        .build())
                .collect(Collectors.toList());

        branchService.saveBranches(branches);
        log.info("Company and its {} branches registered successfully in Master DB", branches.size());

        return company;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void markProvisioningFailed(String companyId, String tenantDatabaseId) {

        Company company = companyRepository.findById(companyId).orElseThrow();

        TenantDatabase tenantDatabase = tenantDatabaseRepository.findById(tenantDatabaseId).orElseThrow();

        company.setStatus(CompanyStatus.PROVISIONING_FAILED);
        tenantDatabase.setStatus(TenantDatabaseStatus.FAILED);
    }

    @Override
    @Transactional
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
        tenantDatabase = tenantDatabaseRepository.save(tenantDatabase);
        log.info("Tenant DB metadata details saved in Master DB");
        return tenantDatabase;
    }

    @Override
    public UserIdentity createUserIdentityInMasterDbForLogin(Company company, @Nonnull RegisterCompanyRequest request) {
   return UserIdentity.builder()
                .company(company)
                .name(request.getAdminName())
                .email(request.getAdminEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.COMPANY_ADMIN)
                .status(AccountStatus.ACTIVE)
                .identityType(IdentityType.TENANT)
                .build();
    }
}

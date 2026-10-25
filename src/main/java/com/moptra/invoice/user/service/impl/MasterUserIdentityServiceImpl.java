package com.moptra.invoice.user.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.tenants.repository.CompanyRepository;
import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.service.MasterUserIdentityService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MasterUserIdentityServiceImpl extends BaseServiceImpl<UserIdentity, String> implements MasterUserIdentityService {
  private final CompanyRepository companyRepository;
  private final PasswordEncoder passwordEncoder;

    public MasterUserIdentityServiceImpl(JpaRepository<UserIdentity, String> repository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createMasterUserIdentity(String companyId, CreateEmployeeRequest request) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException());
        UserIdentity identity = UserIdentity.builder()
                .company(company)
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(AccountStatus.ACTIVE)
                .build();

        save(identity);
    }
}

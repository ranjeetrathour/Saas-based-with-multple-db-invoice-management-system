package com.moptra.invoice.user.service.impl;

import com.moptra.invoice.client.repository.ClientCompanyRepository;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.enums.AccountStatus;
import com.moptra.invoice.tenants.repository.CompanyRepository;
import com.moptra.invoice.user.dto.req.CreateClientUserRequest;
import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.entities.rd.UserIdentity;
import com.moptra.invoice.user.enums.IdentityType;
import com.moptra.invoice.user.repository.UserIdentityRepository;
import com.moptra.invoice.user.service.MasterUserIdentityService;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Manages authentication identities in Master DB (user_identities) for:
 * 1) Vendor internal employees (IdentityType.TENANT, clientCompanyId = null)
 * 2) Restricted Client Portal users (IdentityType.CLIENT, clientCompanyId != null)
 * <p>
 * FUTURE CHANGE POINT:
 * - Add email domain policy validation (e.g., vendor domain vs. client company domain).
 */
@Service
@Slf4j
public class MasterUserIdentityServiceImpl extends BaseServiceImpl<UserIdentity, String> implements MasterUserIdentityService {


    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserIdentityRepository userIdentityRepository;
    private final ClientCompanyRepository clientCompanyRepository;

    public MasterUserIdentityServiceImpl(JpaRepository<UserIdentity, String> repository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, UserIdentityRepository userIdentityRepository, ClientCompanyRepository clientCompanyRepository) {
        super(repository);
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.userIdentityRepository = userIdentityRepository;
        this.clientCompanyRepository = clientCompanyRepository;
    }

    /**
     * in this we will take one more params for useridentity so tenant and platform
     */
    @Override
    public void createMasterUserIdentity(String companyId, @Nonnull CreateEmployeeRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userIdentityRepository.existsByEmail(normalizedEmail)) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "User with email '" + normalizedEmail + "' already exists");
        }
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Vendor company not found with id: " + companyId));
        UserIdentity identity = UserIdentity.builder()
                .company(company)
                .clientCompanyId(null)
                .name(request.getName().trim())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRoleId())
                .identityType(IdentityType.TENANT) // this need to pickup from params
                .status(AccountStatus.ACTIVE)
                .build();
        UserIdentity saved = save(identity);
        log.info("Created TENANT UserIdentity id='{}' email='{}' companyId='{}'", saved.getId(), normalizedEmail, companyId);
    }

    @Override
    public UserIdentity createClientUserIdentity(String companyId, String clientCompanyId, @Nonnull CreateClientUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userIdentityRepository.existsByEmail(normalizedEmail)) {
            throw new GenericException(HttpStatus.CONFLICT.value(), "User with email '" + normalizedEmail + "' already exists");
        }
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Vendor company not found with id: " + companyId));
        UserIdentity identity = UserIdentity.builder()
                .company(company)
                .clientCompanyId(clientCompanyId)
                .name(request.name().trim())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
//                .role(request.role())
                .identityType(IdentityType.CLIENT)
                .status(AccountStatus.ACTIVE)
                .build();
        UserIdentity saved = save(identity);
        log.info("Created CLIENT UserIdentity id='{}' email='{}' companyId='{}' clientCompanyId='{}'", saved.getId(), normalizedEmail, companyId, clientCompanyId);
        return saved;
    }
}

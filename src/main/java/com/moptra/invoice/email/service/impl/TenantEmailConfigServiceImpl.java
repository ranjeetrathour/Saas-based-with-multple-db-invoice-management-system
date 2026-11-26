
package com.moptra.invoice.email.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.authentication.utils.SecurityUtils;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.email.dto.req.TenantEmailConfigReq;
import com.moptra.invoice.email.dto.res.TenantEmailConfigRes;
import com.moptra.invoice.email.entites.TenantEmailConfig;
import com.moptra.invoice.email.repository.TenantEmailConfigRepository;
import com.moptra.invoice.email.service.TenantEmailConfigService;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.service.CompanyRegistrationService;
import com.moptra.invoice.user.enums.IdentityType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.moptra.invoice.comman.Constant.Constants.MASTER_TENANT;

@Service
@Slf4j
public class TenantEmailConfigServiceImpl extends BaseServiceImpl<TenantEmailConfig, String> implements TenantEmailConfigService {


    private final EntityManager entityManager;
    private final CompanyRegistrationService companyRegistrationService;
    private final TenantEmailConfigRepository emailConfigRepository;

    public TenantEmailConfigServiceImpl(JpaRepository<TenantEmailConfig, String> repository, EntityManager entityManager, CompanyRegistrationService companyRegistrationService, TenantEmailConfigRepository emailConfigRepository) {
        super(repository);
        this.entityManager = entityManager;
        this.companyRegistrationService = companyRegistrationService;
        this.emailConfigRepository = emailConfigRepository;
    }

    @Override
    public TenantEmailConfigRes addMailkConfigInfo(TenantEmailConfigReq request) {

        // 1. Validate request
        if (Objects.isNull(request)) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Gmail configuration request cannot be null");
        }

        // 2. Get authenticated user
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();

        if (Objects.isNull(currentUser) || Objects.isNull(currentUser.getCompanyId())) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Authenticated company information not found");
        }

        // 3. Fetch company from master tenant
        Company company = getCompanyFromMasterTenant(currentUser.getCompanyId());

        // 4. Validate Gmail configuration
//        validateGmailConfig(request);

        // 5. Map request to entity
        var tenantConfigData =  GenericMapper.map(request,TenantEmailConfig.class);
        // 5. Explicitly map request to entity
        TenantEmailConfig config = new TenantEmailConfig();
        /**
         * TODO need to encrypt the data for secrete or password
         */

        // 6. Associate configuration with company
        tenantConfigData.setCompany(company);
        String previousTenant = TenantContext.getTenantId();
        // 7. Save configuration
        try {
            TenantContext.setTenantId(MASTER_TENANT);
             tenantConfigData = save(tenantConfigData);
        }finally {
            if (previousTenant == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(previousTenant);
            }
        }

        log.info("mail configuration saved for companyId: {}", currentUser.getCompanyId());

        // 8. Return response
        return new TenantEmailConfigRes("mail configuration saved successfully", tenantConfigData.getSenderEmail());
    }

    @Override
    public TenantEmailConfigRes saveOrUpdateClientConfig(String clientCompanyId, TenantEmailConfigReq request) {
        if (request == null) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Email configuration request cannot be null");
        }
        if (clientCompanyId == null || clientCompanyId.isBlank()) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Client company ID is required");
        }

        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getCompanyId() == null) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Authenticated company information not found");
        }

        Company company = getCompanyFromMasterTenant(currentUser.getCompanyId());

        String previousTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(MASTER_TENANT);

            TenantEmailConfig config = emailConfigRepository
                    .findByCompanyIdAndIdentityTypeAndClientCompanyId(company.getId(), IdentityType.CLIENT.name(), clientCompanyId)
                    .orElseGet(() -> TenantEmailConfig.builder()
                            .company(company)
                            .identityType(IdentityType.CLIENT)
                            .clientCompanyId(clientCompanyId)
                            .build());

            config.setEmailProviderType(request.emailProviderType());
            config.setSenderEmail(request.senderEmail());
            // TODO: Encrypt appPassword and clientSecret before saving
            config.setAppPassword(request.appPassword());
            config.setHost(request.host());
            config.setPort(request.port());
            config.setClientId(request.clientId());
            config.setClientSecret(request.clientSecret());
            config.setTenantId(request.tenantId());
            config.setRefreshToken(request.refreshToken());
            config.setIsActive(true);

            TenantEmailConfig saved = save(config);
            return new TenantEmailConfigRes("Client mail configuration saved successfully", saved.getSenderEmail());
        } finally {
            if (previousTenant == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(previousTenant);
            }
        }
    }

    private @Nonnull Company getCompanyFromMasterTenant(String companyId) {

        String previousTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(MASTER_TENANT);
            return companyRegistrationService.getById(companyId);
        } finally {
            if (previousTenant == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(previousTenant);
            }
        }
    }

    private void validateGmailConfig(@Nonnull TenantEmailConfigReq request) {

        if (request.senderEmail() == null || request.senderEmail().isBlank()) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Sender email is required");
        }

    }

}

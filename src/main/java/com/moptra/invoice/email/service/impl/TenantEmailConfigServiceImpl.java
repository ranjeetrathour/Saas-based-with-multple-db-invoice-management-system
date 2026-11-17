
package com.moptra.invoice.email.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.authentication.utils.SecurityUtils;
import com.moptra.invoice.comman.Constant.Constants;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.email.dto.req.TenantEmailConfigReq;
import com.moptra.invoice.email.dto.res.TenantEmailConfigRes;
import com.moptra.invoice.email.entites.TenantEmailConfig;
import com.moptra.invoice.email.service.TenantEmailConfigService;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.entites.rd.Company;
import com.moptra.invoice.tenants.repository.CompanyRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.moptra.invoice.comman.Constant.Constants.MASTER_TENANT;

@Service
@Slf4j
public class TenantEmailConfigServiceImpl extends BaseServiceImpl<TenantEmailConfig, String> implements TenantEmailConfigService {


    private final EntityManager entityManager;
    private final CompanyRepository companyRepository;

    public TenantEmailConfigServiceImpl(JpaRepository<TenantEmailConfig, String> repository, EntityManager entityManager, CompanyRepository companyRepository) {
        super(repository);
        this.entityManager = entityManager;
        this.companyRepository = companyRepository;
    }

    @Override
    public TenantEmailConfigRes addGmailConfigInfo(TenantEmailConfigReq request) {

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
        // 5. Explicitly map request to entity
        TenantEmailConfig config = new TenantEmailConfig();

        config.setCompany(company);
        config.setSenderEmail(request.senderEmail());
        config.setAppPassword(request.appPassword());
        config.setHost(request.host());
        config.setPort(Integer.valueOf(request.port()));
        config.setEmailProviderType(request.emailProviderType());
        config.setIsActive(true);
        //        config.setAppPassword(request.appPassword());//it should be encrypted in future will do

        // 6. Associate configuration with company
        config.setCompany(company);
        String previousTenant = TenantContext.getTenantId();
        // 7. Save configuration
        TenantEmailConfig savedConfig;
        try {
            TenantContext.setTenantId(MASTER_TENANT);

             config = save(config);
        }finally {
            if (previousTenant == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(previousTenant);
            }
        }

        log.info("Gmail configuration saved for companyId: {}", currentUser.getCompanyId());

        // 8. Return response
        return new TenantEmailConfigRes("Gmail configuration saved successfully", config.getSenderEmail());
    }

    private @Nonnull Company getCompanyFromMasterTenant(String companyId) {

        String previousTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(MASTER_TENANT);

            return companyRepository.findById(companyId).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), Constants.COMPANY_NOT_FOUND));

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

        // Add additional validations as per your requirements.
    }

    private void logCurrentDatabase() {

        Object[] dbInfo = (Object[]) entityManager
                .createNativeQuery("""
                SELECT current_database(),
                       current_schema(),
                       current_user,
                       inet_server_addr(),
                       inet_server_port()
            """)
                .getSingleResult();

        log.info("DATABASE: {}", dbInfo[0]);
        log.info("SCHEMA: {}", dbInfo[1]);
        log.info("DB USER: {}", dbInfo[2]);
        log.info("DB HOST: {}", dbInfo[3]);
        log.info("DB PORT: {}", dbInfo[4]);
    }
}

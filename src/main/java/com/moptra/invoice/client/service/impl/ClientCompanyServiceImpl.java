package com.moptra.invoice.client.service.impl;

import com.moptra.invoice.client.dto.req.ClientCompanyRequest;
import com.moptra.invoice.client.dto.res.ClientCompanyResponse;
import com.moptra.invoice.client.entities.ClientBranch;
import com.moptra.invoice.client.entities.ClientCompany;
import com.moptra.invoice.client.repository.ClientCompanyRepository;
import com.moptra.invoice.client.service.ClientBranchService;
import com.moptra.invoice.client.service.ClientCompanyService;
import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for creating and managing Client Companies (e.g., Petco)
 * and their optional branches inside the active Vendor Tenant DB.
 *
 * FLOW CONTEXT:
 * - ClientCompany and ClientBranch live in the Tenant DB so projects, per-employee
 *   SOWs, allocations, and billing stay isolated per main vendor tenant.
 *
 * FUTURE CHANGE POINT:
 * - Add role/identity authorization guard (blocking CLIENT portal users and checking
 *   authorized vendor role/permission) once security context helpers are standardized here.
 * - Implement getClientCompanyById, getAllClientCompanies, and updateClientCompany.
 */
@Service
@Slf4j
public class ClientCompanyServiceImpl extends BaseServiceImpl<ClientCompany, String> implements ClientCompanyService {

    private final ClientCompanyRepository clientCompanyRepository;
    private final ClientBranchService clientBranchService;

    public ClientCompanyServiceImpl(
            JpaRepository<ClientCompany, String> repository,
            ClientCompanyRepository clientCompanyRepository,
            ClientBranchService clientBranchService) {
        super(repository);
        this.clientCompanyRepository = clientCompanyRepository;
        this.clientBranchService = clientBranchService;
    }

    /**
     * Validates active tenant DB context, checks duplicate client companyCode within
     * that tenant DB, and persists ClientCompany + optional ClientBranches.
     *
     * FUTURE CHANGE POINT: Also provision restricted CLIENT_ADMIN identity in Master DB
     * if initial client portal credentials are requested during onboarding.
     */
    @Override
    @Transactional
    public ClientCompanyResponse createClientCompany(@NonNull ClientCompanyRequest request) {
        try {
            // 1. Verify request is routed to a valid Tenant DB (not blank or master DB)
            String currentTenant = TenantContext.getTenantId();
            if (currentTenant == null || currentTenant.isBlank() || "master".equalsIgnoreCase(currentTenant.trim())) {
                throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Valid tenant context is required to onboard a client company");
            }

            // FUTURE CHANGE POINT: Validate authenticated user's role/permission and ensure
            // IdentityType.CLIENT users cannot onboard client companies.

            String normalizedCode = request.companyCode().trim().toUpperCase();
            log.info("Onboarding client company code='{}' into tenantContext='{}'", normalizedCode, currentTenant);

            // 2. Check uniqueness within the current Tenant DB
            if (clientCompanyRepository.existsByCompanyCode(normalizedCode)) {
                throw new GenericException(HttpStatus.CONFLICT.value(), "Client company with code '" + normalizedCode + "' already exists");
            }

            ClientCompany clientCompany = GenericMapper.map(request, ClientCompany.class);
            clientCompany.setCompanyCode(normalizedCode);

            ClientCompany savedDataForCompany = save(clientCompany);

            // 3. Map and save optional branches under this client company
            if (request.branches() != null && !request.branches().isEmpty()) {
                List<ClientBranch> clientBranches = request.branches().stream()
                        .map(branchDto -> {
                            ClientBranch branch = GenericMapper.map(branchDto, ClientBranch.class);
                            branch.setBranchCode(branchDto.branchCode().trim().toUpperCase());
                            branch.setClientCompany(savedDataForCompany);
                            branch.setIsActive(branchDto.isActive() != null ? branchDto.isActive() : true);
                            return branch;
                        })
                        .toList();

                clientBranchService.saveClientBranches(clientBranches);
            }

            log.info("Saved client company id='{}' (code='{}') in tenantContext='{}'", savedDataForCompany.getId(), normalizedCode, currentTenant);

            return new ClientCompanyResponse(
                    savedDataForCompany.getCompanyName(),
                    savedDataForCompany.getCompanyCode(),
                    savedDataForCompany.getEmail(),
                    savedDataForCompany.getPhone(),
                    savedDataForCompany.getGst(),
                    savedDataForCompany.getBillingAddress(),
                    savedDataForCompany.getCity(),
                    savedDataForCompany.getState(),
                    savedDataForCompany.getCountry(),
                    List.of(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        } finally {
            // 4. Clear thread-local tenant context to avoid cross-request leakage
            // FUTURE CHANGE POINT: Ensure this does not conflict with filter-level TenantContext.clear()
            TenantContext.clear();
        }
    }

    @Override
    public ClientCompanyResponse getClientCompanyById(Long id) {
        // FUTURE CHANGE POINT: Fetch client company from active tenant DB and map branches
        return null;
    }

    @Override
    public List<ClientCompanyResponse> getAllClientCompanies() {
        // FUTURE CHANGE POINT: List client companies scoped to active tenant DB
        return List.of();
    }

    @Override
    public ClientCompanyResponse updateClientCompany(Long id, ClientCompanyRequest request) {
        // FUTURE CHANGE POINT: Update client company and branches within active tenant DB
        return null;
    }
}
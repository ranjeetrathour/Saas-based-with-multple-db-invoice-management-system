package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.authentication.security.CustomUserDetails;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.context.TenantContext;
import com.moptra.invoice.tenants.dto.req.ClientCompanyRequest;
import com.moptra.invoice.tenants.dto.res.ClientCompanyResponse;
import com.moptra.invoice.tenants.entites.rd.ClientBranch;
import com.moptra.invoice.tenants.entites.rd.ClientCompany;
import com.moptra.invoice.tenants.repository.rd.ClientCompanyRepository;
import com.moptra.invoice.tenants.service.ClientBranchService;
import com.moptra.invoice.tenants.service.ClientCompanyService;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientCompanyServiceImpl extends BaseServiceImpl<ClientCompany, String> implements ClientCompanyService {

    private final ClientCompanyRepository clientCompanyRepository;
    private final ClientBranchService clientBranchService;

    public ClientCompanyServiceImpl(JpaRepository<ClientCompany, String> repository, ClientCompanyRepository clientCompanyRepository, ClientBranchService clientBranchService) {
        super(repository);
        this.clientCompanyRepository = clientCompanyRepository;
        this.clientBranchService = clientBranchService;
    }

    @Override
    @Transactional
    public ClientCompanyResponse createClientCompany(@NonNull ClientCompanyRequest request) {
        try {
            if (clientCompanyRepository.existsByCompanyCode(request.companyCode())) {
                throw new IllegalArgumentException("Company code already exists");
            }

            // 2. Map and save the main company
            ClientCompany clientCompany = GenericMapper.map(request, ClientCompany.class);
            ClientCompany savedDataForCompany = save(clientCompany);

            // 3. Map and save branches
            if (request.branches() != null && !request.branches().isEmpty()) {
                List<ClientBranch> clientBranches = request.branches().stream()
                        .map(branchDto -> {
                            ClientBranch branch = GenericMapper.map(branchDto, ClientBranch.class);
                            branch.setClientCompany(savedDataForCompany);
                            branch.setIsActive(true);
                            return branch;
                        })
                        .toList();

                clientBranchService.saveClientBranches(clientBranches);
            }

            return GenericMapper.map(savedDataForCompany, ClientCompanyResponse.class);

        } finally {
            // 4. ALWAYS clear the thread local to prevent tenant data leakage across requests!
            TenantContext.clear();
        }
    }

    @Override
    public ClientCompanyResponse getClientCompanyById(Long id) {
        return null;
    }

    @Override
    public List<ClientCompanyResponse> getAllClientCompanies() {
        return List.of();
    }

    @Override
    public ClientCompanyResponse updateClientCompany(Long id, ClientCompanyRequest request) {
        return null;
    }
}

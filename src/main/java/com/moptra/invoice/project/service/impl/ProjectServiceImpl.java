package com.moptra.invoice.project.service.impl;

import com.moptra.invoice.client.entities.ClientBranch;
import com.moptra.invoice.client.entities.ClientCompany;
import com.moptra.invoice.client.repository.ClientBranchRepository;
import com.moptra.invoice.client.repository.ClientCompanyRepository;
import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.project.entites.Project;
import com.moptra.invoice.project.dto.req.ProjectRequest;
import com.moptra.invoice.project.dto.res.ProjectResponse;
import com.moptra.invoice.project.repository.ProjectRepository;
import com.moptra.invoice.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class ProjectServiceImpl extends BaseServiceImpl<Project, String> implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientCompanyRepository companyRepository;
    private final ClientBranchRepository branchRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, ClientCompanyRepository companyRepository, ClientBranchRepository branchRepository
                              ) {
        super(projectRepository);
        this.projectRepository = projectRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public ProjectResponse insertProjectEntry(ProjectRequest projectRequest) {
        // 1. Check if request is null
        if (Objects.isNull(projectRequest)) {
            log.error("ProjectRequest cannot be null");
            throw new IllegalArgumentException("Project request data cannot be null");
        }

        // 2. Check if project code already exists (to prevent unique constraint violation)
        if (projectRepository.existsByProjectCode(projectRequest.projectCode())) {
            log.error("Project with code {} already exists", projectRequest.projectCode());
            throw new IllegalArgumentException("Project with code '" + projectRequest.projectCode() + "' already exists.");
        }

        // 3. Validate and fetch Client Company
        ClientCompany clientCompany = companyRepository.findById(projectRequest.clientCompanyId())
                .orElseThrow(() -> {
                    log.error("Client company not found with ID: {}", projectRequest.clientCompanyId());
                    return new IllegalArgumentException("Client company not found with ID: " + projectRequest.clientCompanyId());
                });

        // 4. Validate and fetch Branch (if provided)
        ClientBranch branch = null;
        if (projectRequest.branchId() != null && !projectRequest.branchId().isBlank()) {
            branch = branchRepository.findById(projectRequest.branchId())
                    .orElseThrow(() -> {
                        log.error("Branch not found with ID: {}", projectRequest.branchId());
                        return new IllegalArgumentException("Branch not found with ID: " + projectRequest.branchId());
                    });
        }

        // 5. Map basic fields using GenericMapper
        Project project = GenericMapper.map(projectRequest, Project.class);

        // 6. Set relational entities manually
        project.setClientCompany(clientCompany);
        project.setBranch(branch);

        // 7. Save project
        save(project);
        log.info("Successfully created project with code: {}", project.getProjectCode());
        return GenericMapper.map(project, ProjectResponse.class);
    }
}
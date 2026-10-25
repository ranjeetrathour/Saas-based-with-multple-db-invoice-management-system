package com.moptra.invoice.project.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.project.dto.req.ProjectAllocationRequest;
import com.moptra.invoice.project.entites.Project;
import com.moptra.invoice.project.entites.ProjectAllocation;
import com.moptra.invoice.project.repository.ProjectRepository;
import com.moptra.invoice.project.service.ProjectAllocationService;
import com.moptra.invoice.user.entities.rd.Employee;
import com.moptra.invoice.user.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
public class ProjectAllocationServiceImpl extends BaseServiceImpl<ProjectAllocation, String> implements ProjectAllocationService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectAllocationServiceImpl(JpaRepository<ProjectAllocation, String> repository, ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
        super(repository);
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void allocateProject(ProjectAllocationRequest request) {
        log.info("Starting project allocation");

        if (Objects.isNull(request)) {
            log.warn("Project allocation failed: request is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project allocation request cannot be null");
        }

        if (request.getEmployee().getId() == null || request.getEmployee().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID is required");
        }

        if (request.getProject().getId() == null || request.getProject().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project ID is required");
        }

        if (request.getBillingHourlyRate() == null || request.getBillingHourlyRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Billing hourly rate must be zero or greater");
        }

        if (request.getStartDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is required");
        }

        Employee employee = employeeRepository.findById(request.getEmployee().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        Project project = projectRepository.findById(request.getProject().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        ProjectAllocation allocation = ProjectAllocation.builder()
                .employee(employee)
                .project(project)
                .billingHourlyRate(request.getBillingHourlyRate())
                .startDate(request.getStartDate())
                .isActive(request.getIsActive() != null
                        ? request.getIsActive() : true)
                .build();

        save(allocation);

        log.info("Project allocation created successfully");
    }
}
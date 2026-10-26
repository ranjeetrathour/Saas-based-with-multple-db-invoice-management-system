package com.moptra.invoice.project.service;

import com.moptra.invoice.project.dto.req.ProjectAllocationRequest;
import com.moptra.invoice.project.dto.res.EmployeeActiveProjectResponse;

public interface ProjectAllocationService {
    void allocateProject(ProjectAllocationRequest request);
    EmployeeActiveProjectResponse getLoggedInUserCurrentActiveProject();
}

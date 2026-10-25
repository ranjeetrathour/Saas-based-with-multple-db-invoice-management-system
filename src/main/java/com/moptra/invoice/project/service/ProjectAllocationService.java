package com.moptra.invoice.project.service;

import com.moptra.invoice.project.dto.req.ProjectAllocationRequest;

public interface ProjectAllocationService {
    void allocateProject(ProjectAllocationRequest request);
}

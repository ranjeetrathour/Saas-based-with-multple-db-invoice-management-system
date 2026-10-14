package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.ProjectAllocationRequest;

public interface ProjectAllocationService {
    void allocateProject(ProjectAllocationRequest request);
}

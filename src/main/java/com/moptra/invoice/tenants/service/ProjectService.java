package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.dto.req.ProjectRequest;
import com.moptra.invoice.tenants.dto.res.ProjectResponse;

public interface ProjectService {
    ProjectResponse insertProjectEntry(ProjectRequest projectRequest);
}

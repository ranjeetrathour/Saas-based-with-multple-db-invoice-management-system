package com.moptra.invoice.project.service;

import com.moptra.invoice.project.dto.req.ProjectRequest;
import com.moptra.invoice.project.dto.res.ProjectResponse;

public interface ProjectService {
    ProjectResponse insertProjectEntry(ProjectRequest projectRequest);
}

package com.moptra.invoice.project.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeActiveProjectResponse {
    private String name;
    private String projectCode; // e.g., "HCL-BNK-001"
}

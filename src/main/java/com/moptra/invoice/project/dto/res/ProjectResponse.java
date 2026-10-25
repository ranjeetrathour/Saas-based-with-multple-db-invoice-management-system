package com.moptra.invoice.project.dto.res;

import com.moptra.invoice.tenants.enums.ProjectBillingType;
import com.moptra.invoice.tenants.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private String name;
    private String projectCode;
    private String description;
    private String clientCompanyId;
    private String branchId;
    private ProjectBillingType billingType;
    private BigDecimal totalBudget;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
}
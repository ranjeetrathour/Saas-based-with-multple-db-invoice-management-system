package com.moptra.invoice.project.dto.req;

import com.moptra.invoice.tenants.enums.ProjectBillingType;
import com.moptra.invoice.tenants.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank(message = "Project name is required")
        String name,

        @NotBlank(message = "Project code is required")
        String projectCode,

        String description,

        @NotBlank(message = "Client company ID is required")
        String clientCompanyId,

        String branchId, // Optional

        @NotNull(message = "Billing type is required")
        ProjectBillingType billingType,

        BigDecimal totalBudget,

        @NotBlank(message = "Currency is required")
        String currency,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate, // Optional

        @NotNull(message = "Status is required")
        ProjectStatus status
) {
}
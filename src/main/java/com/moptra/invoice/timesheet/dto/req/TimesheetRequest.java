package com.moptra.invoice.timesheet.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record TimesheetRequest(
        @NotBlank(message = "Employee ID is required")
        String employeeId,

        @NotBlank(message = "Company ID is required")
        String companyId,

        @NotNull(message = "Week start date is required")
        LocalDate weekStartDate,

        @NotNull(message = "Week end date is required")
        LocalDate weekEndDate,

        @NotNull(message = "Year is required")
        Integer year,

        Map<String, Boolean> dayOffToggles, // Key: Date string ("2026-10-20"), Value: boolean

        @Valid
        List<TimesheetRowRequest> rows
)
{};
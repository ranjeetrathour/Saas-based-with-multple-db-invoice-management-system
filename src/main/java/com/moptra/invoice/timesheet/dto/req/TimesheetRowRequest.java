package com.moptra.invoice.timesheet.dto.req;

import java.util.Map;

public record TimesheetRowRequest(
        String rowId,
        String rowType,
        String projectId,
        String projectName,
        Map<String, String> dailyHours
) {}
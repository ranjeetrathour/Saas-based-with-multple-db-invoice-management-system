
package com.moptra.invoice.tenants.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetResponse {

    private String id;
    private String employeeId;
    private String companyId;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private Integer weekNumber;
    private Integer year;
    private String status;

    private Map<String, Boolean> dayOffToggles;
    private List<TimesheetRowResponse> rows;

    private Double totalWeeklyHours;
    private ApprovalFlowResponse approvalFlow;

    private Instant createdAt;
    private Instant updatedAt;
}

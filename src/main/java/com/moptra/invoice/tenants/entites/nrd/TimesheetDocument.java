package com.moptra.invoice.tenants.entites.nrd;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Document("timesheet")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimesheetDocument {

    @Id
    private String id;

    @Field("employee_id")
    private String employeeId;

    @Field("company_id")
    private String companyId;

    @Field("week_start_date")
    private LocalDate weekStartDate;

    @Field("week_end_date")
    private LocalDate weekEndDate;

    @Field("year")
    private Integer year;

    private String status; // PENDING, SUBMITTED, APPROVED, REJECTED

    @Field("day_off_toggles")
    private Map<String, Boolean> dayOffToggles; // Key: Date ("2026-10-20"), Value: boolean

    @Field("rows")
    private List<TimesheetRow> rows;

    @Field("total_weekly_hours")
    private Double totalWeeklyHours;

    @Field("approval_flow")
    private ApprovalFlow approvalFlow;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;
}

package com.moptra.invoice.tenants.entites.nrd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimesheetRow {
    @Field("row_id")
    private String rowId; // Unique key generated per row on the UI

    @Field("row_type")
    private String rowType; // "PROJECT" or "LEAVE"

    @Field("project_id")
    private String projectId; // Nullable for LEAVE / Time Off

    @Field("project_name")
    private String projectName; // Displays on UI dropdown (e.g., "260007_Dotcom")

    @Field("daily_hours")
    private Map<String, String> dailyHours; // Key: Date ("2026-10-18"), Value: Time ("08:00")

    @Field("weekly_total_hours")
    private Double weeklyTotalHours;
}

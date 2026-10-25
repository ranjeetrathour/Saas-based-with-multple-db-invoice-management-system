
package com.moptra.invoice.timesheet.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetRowResponse {

    private String rowId;
    private String rowType;
    private String projectId;
    private String projectName;
    private Map<String, String> dailyHours;
    private Double weeklyTotalHours;
}

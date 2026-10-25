package com.moptra.invoice.timesheet.service;



import com.moptra.invoice.timesheet.dto.req.TimesheetRequest;
import com.moptra.invoice.timesheet.dto.res.TimesheetResponse;

import java.time.LocalDate;
import java.util.List;

public interface TimesheetService {
    TimesheetResponse saveOrUpdateDraft(TimesheetRequest request);
    TimesheetResponse submitTimesheet(String timesheetId);
    TimesheetResponse getTimesheetByEmployeeAndWeek(String employeeId, LocalDate weekStartDate);
    TimesheetResponse getTimesheetById(String id);
    List<TimesheetResponse> getTimesheetsByEmployee(String employeeId);
}

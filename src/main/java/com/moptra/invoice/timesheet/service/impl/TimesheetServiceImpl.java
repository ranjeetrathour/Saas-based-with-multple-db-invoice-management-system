
package com.moptra.invoice.timesheet.service.impl;

import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.timesheet.dto.req.TimesheetRequest;
import com.moptra.invoice.timesheet.dto.req.TimesheetRowRequest;
import com.moptra.invoice.timesheet.dto.res.TimesheetResponse;
import com.moptra.invoice.timesheet.entites.nrd.TimesheetDocument;
import com.moptra.invoice.timesheet.entites.nrd.TimesheetRow;
import com.moptra.invoice.timesheet.repository.TimesheetRepository;
import com.moptra.invoice.timesheet.service.TimesheetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final MongoTemplate mongoTemplate;


    /**
     * need to add many validation here like user has valid project for this timesheet or emp id is valid etc
     * @param request
     * @return
     */
    @Override
    public TimesheetResponse saveOrUpdateDraft(TimesheetRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Timesheet request must not be null");
        }
        log.info("Mongo database: {}", mongoTemplate.getDb().getName());
        Optional<TimesheetDocument> existing = timesheetRepository.findByEmployeeIdAndWeekStartDate(request.employeeId(), request.weekStartDate());

        TimesheetDocument document;

        if (existing.isPresent()) {
            document = existing.get();

            if ("SUBMITTED".equalsIgnoreCase(document.getStatus())) {
                throw new IllegalStateException("A submitted timesheet cannot be updated");
            }

            // Update the existing document's fields.
            TimesheetDocument updated = GenericMapper.map(request, TimesheetDocument.class);

            updated.setId(document.getId());
            updated.setStatus("PENDING");
            document = updated;
        } else {
            document = GenericMapper.map(request, TimesheetDocument.class);
            document.setStatus("PENDING");
        }
        var leaveHours = request.rows()
                .stream().filter(row -> row.rowType().equalsIgnoreCase("leave"))
                .findFirst()
                .map(row -> row.dailyHours().values()
                        .stream()
                        .filter(value -> value != null && !value.isBlank())
                        .mapToDouble(Double::parseDouble)
                        .sum())
                .orElse(0d);
        document.setTotalWeeklyHours((40 - leaveHours));

        TimesheetDocument saved = timesheetRepository.save(document);

        log.info("Saved timesheet with id {}", saved.getId());

        return GenericMapper.map(saved, TimesheetResponse.class);
    }

    @Override
    public TimesheetResponse submitTimesheet(String timesheetId) {
        return null;
    }

    @Override
    public TimesheetResponse getTimesheetByEmployeeAndWeek(String employeeId, LocalDate weekStartDate) {
        return null;
    }

    @Override
    public TimesheetResponse getTimesheetById(String id) {
        return null;
    }

    @Override
    public List<TimesheetResponse> getTimesheetsByEmployee(
            String employeeId) {
        return List.of();
    }
}


package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.comman.mappers.GenericMapper;
import com.moptra.invoice.tenants.dto.req.TimesheetRequest;
import com.moptra.invoice.tenants.dto.res.TimesheetResponse;
import com.moptra.invoice.tenants.entites.nrd.TimesheetDocument;
import com.moptra.invoice.tenants.repository.nrd.TimesheetRepository;
import com.moptra.invoice.tenants.service.TimesheetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final MongoTemplate mongoTemplate;


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

        TimesheetDocument saved = timesheetRepository.save(document);

        log.info("Saved timesheet with id {}", saved.getId());

        return GenericMapper.map(saved, TimesheetResponse.class);
    }

    @Override
    public TimesheetResponse submitTimesheet(String timesheetId) {
        return null;
    }

    @Override
    public TimesheetResponse getTimesheetByEmployeeAndWeek(
            String employeeId, LocalDate weekStartDate) {
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

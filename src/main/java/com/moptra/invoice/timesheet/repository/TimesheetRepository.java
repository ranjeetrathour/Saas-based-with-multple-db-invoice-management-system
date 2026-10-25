package com.moptra.invoice.timesheet.repository;

import com.moptra.invoice.timesheet.entites.nrd.TimesheetDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;


public interface TimesheetRepository extends MongoRepository<TimesheetDocument, String> {
    Optional<TimesheetDocument> findByEmployeeIdAndWeekStartDate(String empId, LocalDate weekStartDate);
}

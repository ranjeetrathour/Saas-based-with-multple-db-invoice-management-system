package com.moptra.invoice.tenants.repository.nrd;

import com.moptra.invoice.tenants.entites.nrd.TimesheetDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;


public interface TimesheetRepository extends MongoRepository<TimesheetDocument, String> {
    Optional<TimesheetDocument> findByEmployeeIdAndWeekStartDate(String empId, LocalDate weekStartDate);
}

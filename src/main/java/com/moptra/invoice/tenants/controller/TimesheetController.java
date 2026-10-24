package com.moptra.invoice.tenants.controller;

import com.moptra.invoice.tenants.dto.req.TimesheetRequest;
import com.moptra.invoice.tenants.dto.res.TimesheetResponse;
import com.moptra.invoice.tenants.service.TimesheetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/timesheet")
public class TimesheetController {
    private final TimesheetService timesheetService;

    @PostMapping("/save")
    public ResponseEntity<TimesheetResponse> saveOrUpdateDraft(@Valid @RequestBody TimesheetRequest request) {
        return new ResponseEntity<>(timesheetService.saveOrUpdateDraft(request), HttpStatus.OK);
    }
}

package com.moptra.invoice.tenants.controller;

import com.moptra.invoice.tenants.dto.req.ProjectAllocationRequest;
import com.moptra.invoice.tenants.service.ProjectAllocationService;
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
@RequestMapping("/api/project-allocation")
public class ProjectAllocationController {
    private final ProjectAllocationService projectAllocationService;

    @PostMapping
    public ResponseEntity<?> allocateProjectToEmployee(@Valid @RequestBody ProjectAllocationRequest request){
        projectAllocationService.allocateProject(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

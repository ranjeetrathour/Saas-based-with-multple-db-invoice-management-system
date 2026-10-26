package com.moptra.invoice.project.controller;

import com.moptra.invoice.project.dto.req.ProjectAllocationRequest;
import com.moptra.invoice.project.dto.res.EmployeeActiveProjectResponse;
import com.moptra.invoice.project.service.ProjectAllocationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/active-project")
    public ResponseEntity<EmployeeActiveProjectResponse> getLoggedInEmployeeActiveAllocatedProject(){
        EmployeeActiveProjectResponse loggedInUserCurrentActiveProject = projectAllocationService.getLoggedInUserCurrentActiveProject();
        return ResponseEntity.status(HttpStatus.OK).body(loggedInUserCurrentActiveProject);
    }
}

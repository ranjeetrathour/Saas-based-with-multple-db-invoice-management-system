package com.moptra.invoice.project.repository;

import com.moptra.invoice.project.entites.ProjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation, String> {

    @Transactional(readOnly = true)
    @Query(nativeQuery = true, value = "SELECT * FROM project_allocations WHERE employee_id =?1 AND is_active =true")
    Optional<ProjectAllocation> findByEmployeeIdAndISActiveTrue(String employeeId);
}

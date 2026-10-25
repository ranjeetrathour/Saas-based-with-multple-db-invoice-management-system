package com.moptra.invoice.project.repository;

import com.moptra.invoice.project.entites.ProjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation, String> {
}

package com.moptra.invoice.project.repository.rd;

import com.moptra.invoice.project.entites.rd.ProjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation, String> {
}

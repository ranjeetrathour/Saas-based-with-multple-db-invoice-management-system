package com.moptra.invoice.tenants.repository.rd;

import com.moptra.invoice.tenants.entites.rd.ProjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation, String> {
}

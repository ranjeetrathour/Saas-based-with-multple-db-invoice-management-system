package com.moptra.invoice.tenants.repository;

import com.moptra.invoice.tenants.entites.rd.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, String> {
}

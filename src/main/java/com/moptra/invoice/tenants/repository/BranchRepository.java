package com.moptra.invoice.tenants.repository;

import com.moptra.invoice.tenants.entites.rd.Branch;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, String> {
    boolean findByBranchCode(@NotBlank(message = "Branch code is required") String branchCode);

    List<Branch> findByCompanyId(String id);
}

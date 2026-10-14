package com.moptra.invoice.tenants.repository.rd;

import com.moptra.invoice.tenants.entites.rd.ClientBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientBranchRepository extends JpaRepository<ClientBranch, String> {
    List<ClientBranch> findByClientCompanyId(Long clientCompanyId);
}

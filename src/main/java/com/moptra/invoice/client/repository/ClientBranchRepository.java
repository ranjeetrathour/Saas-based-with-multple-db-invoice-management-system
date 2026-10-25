package com.moptra.invoice.client.repository;

import com.moptra.invoice.client.entities.ClientBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientBranchRepository extends JpaRepository<ClientBranch, String> {
    List<ClientBranch> findByClientCompanyId(Long clientCompanyId);
}

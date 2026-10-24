package com.moptra.invoice.client.repository.rd;

import com.moptra.invoice.client.entity.rd.ClientBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientBranchRepository extends JpaRepository<ClientBranch, String> {
    List<ClientBranch> findByClientCompanyId(Long clientCompanyId);
}

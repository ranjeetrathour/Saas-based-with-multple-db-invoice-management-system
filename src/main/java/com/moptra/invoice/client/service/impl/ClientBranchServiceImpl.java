package com.moptra.invoice.client.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.client.entities.ClientBranch;
import com.moptra.invoice.client.service.ClientBranchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClientBranchServiceImpl extends BaseServiceImpl<ClientBranch, String> implements ClientBranchService {
    public ClientBranchServiceImpl(JpaRepository<ClientBranch, String> repository) {
        super(repository);
    }

    @Override
    public void saveClientBranches(List<ClientBranch> branches) {
        try {
            saveAll(branches);
            log.info("save all branches for client {} ", branches.get(0).getClientCompany().getCompanyName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

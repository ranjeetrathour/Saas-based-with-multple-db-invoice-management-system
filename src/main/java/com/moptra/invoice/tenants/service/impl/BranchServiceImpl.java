package com.moptra.invoice.tenants.service.impl;

import com.moptra.invoice.comman.service.impl.BaseServiceImpl;
import com.moptra.invoice.tenants.entites.rd.Branch;
import com.moptra.invoice.tenants.service.BranchService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImpl extends BaseServiceImpl<Branch, String>  implements BranchService {
    public BranchServiceImpl(JpaRepository<Branch, String> repository) {
        super(repository);
    }

    @Override
    public void saveBranches(List<Branch> branches) {
        saveAll(branches);
    }
}

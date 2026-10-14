package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.entites.rd.Branch;

import java.util.List;

public interface BranchService {
    void saveBranches(List<Branch> branches);
}

package com.moptra.invoice.tenants.service;

import com.moptra.invoice.tenants.entites.rd.ClientBranch;

import java.util.List;

public interface ClientBranchService {
    void saveClientBranches(List<ClientBranch> branches);
}

package com.moptra.invoice.client.service;

import com.moptra.invoice.client.entities.ClientBranch;

import java.util.List;

public interface ClientBranchService {
    void saveClientBranches(List<ClientBranch> branches);
}

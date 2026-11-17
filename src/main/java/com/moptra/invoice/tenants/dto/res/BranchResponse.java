package com.moptra.invoice.tenants.dto.res;

public record BranchResponse(String branchId, String branchCode,
                             String branchAddress, String branchCity,
                             String branchCountry) {
}

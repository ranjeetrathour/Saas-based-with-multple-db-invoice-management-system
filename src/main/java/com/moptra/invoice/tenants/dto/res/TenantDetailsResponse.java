package com.moptra.invoice.tenants.dto.res;

import java.util.List;

public record TenantDetailsResponse(
        String companyId,
        String companyName,
        String companyCode,
        String companyEmail,
        String companyPhone,
        String website,
        String status,
        List<BranchResponse> branches
) {
}
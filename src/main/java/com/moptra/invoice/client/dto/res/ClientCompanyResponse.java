
package com.moptra.invoice.client.dto.res;


import java.time.LocalDateTime;
import java.util.List;

public record ClientCompanyResponse(
        String companyName,
        String companyCode,
        String email,
        String phone,
        String gst,
        String billingAddress,
        String city,
        String state,
        String country,
        List<ClientBranchResponse> branches,
        String adminName,
        String adminEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
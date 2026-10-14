package com.moptra.invoice.tenants.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
public record ClientCompanyRequest(
        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Company code is required")
        String companyCode,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        String phone,
        String gst,
        String billingAddress,
        String city,
        String state,
        String country,

        @Valid
        List<ClientBranchRequest> branches
) {}
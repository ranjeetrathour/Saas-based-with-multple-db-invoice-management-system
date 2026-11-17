package com.moptra.invoice.tenants.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCompanyResponse {

    private String companyId; //refer to pk from company entity

    private Long userId;

    private String companyName;

    private String adminEmail;

    private String password;

    private String status;

    private String message;
}
package com.moptra.invoice.tenants.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterCompanyResponse {

    private Long companyId;

    private Long userId;

    private String companyName;

    private String adminEmail;

    private String password;

    private String databaseName;

    private String status;

    private String message;
}
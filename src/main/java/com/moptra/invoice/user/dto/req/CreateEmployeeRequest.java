package com.moptra.invoice.user.dto.req;

import com.moptra.invoice.user.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateEmployeeRequest {

    // User information
    private String name;
    private String email;
    private String password;
    private UserRole role;

    // Employee information
    private String employeeId;
    private String designation;
    private String department;
    private String branch;
    private LocalDate joiningDate;
    private BigDecimal hourlyCost;

    private Long reportManagerId;
    private Long teamLeadId;
}
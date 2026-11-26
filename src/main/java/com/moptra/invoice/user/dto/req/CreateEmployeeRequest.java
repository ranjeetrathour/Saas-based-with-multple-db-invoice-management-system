package com.moptra.invoice.user.dto.req;


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
    private String roleId; //need to take role id in req

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
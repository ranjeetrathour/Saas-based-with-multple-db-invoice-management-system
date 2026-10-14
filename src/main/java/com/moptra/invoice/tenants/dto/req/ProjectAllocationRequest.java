package com.moptra.invoice.tenants.dto.req;

import com.moptra.invoice.tenants.entites.rd.Project;
import com.moptra.invoice.user.entities.rd.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAllocationRequest {

    private Employee employee;

    private Project project;

    private BigDecimal billingHourlyRate;

    private LocalDate startDate;

    private Boolean isActive = true;
}

package com.moptra.invoice.project.entites;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.user.entities.rd.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "project_allocations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAllocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "billing_hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal billingHourlyRate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
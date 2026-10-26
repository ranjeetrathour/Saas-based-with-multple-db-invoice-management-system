package com.moptra.invoice.user.entities.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;

    private String designation;

    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee reportManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_lead_id")
    private Employee teamLead;

    @Column(name = "branch_id")
    private String branch;

    @Column(name = "joining_date")
    private LocalDate joiningDate;


    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", unique = true)
    @ToString.Exclude
    private User user;
}
package com.moptra.invoice.user.entities.rd;

import jakarta.persistence.Entity;

import com.moptra.invoice.comman.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "employee")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity{

    private String employeeId;

    private String designation;

    private String reportManager;

    private String teamLead;

    private String department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @ToString.Exclude
    private User user;

    /**
     * user will align with an project so it has mapping with the project or client anyone
     */

}

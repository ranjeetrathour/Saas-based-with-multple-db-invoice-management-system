package com.moptra.invoice.user.repository;

import com.moptra.invoice.user.entities.rd.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Override
    Optional<Employee> findById(String s);
}

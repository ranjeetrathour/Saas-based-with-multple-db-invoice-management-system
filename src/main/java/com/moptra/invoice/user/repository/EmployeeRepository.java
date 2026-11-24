package com.moptra.invoice.user.repository;

import com.moptra.invoice.user.entities.rd.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    boolean existsByEmployeeId(String employeeId);

    Optional<Employee> findByEmployeeId(String employeeId);
}
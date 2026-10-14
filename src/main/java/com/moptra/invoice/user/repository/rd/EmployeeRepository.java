package com.moptra.invoice.user.repository.rd;

import com.moptra.invoice.user.entities.rd.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
}

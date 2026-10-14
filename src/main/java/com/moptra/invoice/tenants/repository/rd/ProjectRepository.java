package com.moptra.invoice.tenants.repository.rd;

import com.moptra.invoice.tenants.entites.rd.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String>{
    boolean existsByProjectCode(String s);
}

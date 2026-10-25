package com.moptra.invoice.project.repository.rd;

import com.moptra.invoice.project.entites.rd.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String>{
    boolean existsByProjectCode(String s);
}

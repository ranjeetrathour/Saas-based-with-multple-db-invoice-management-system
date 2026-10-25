package com.moptra.invoice.project.repository;

import com.moptra.invoice.project.entites.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String>{
    boolean existsByProjectCode(String s);
}

package com.lead.generation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lead.generation.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}

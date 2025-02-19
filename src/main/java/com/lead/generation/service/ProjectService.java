package com.lead.generation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.lead.generation.entity.Project;

public interface ProjectService {
	public Project Save(Project project) throws Exception;

	public Project  update(Long id,String projectName);

	public void delete(Long id);

	public Optional<Project> getById(Long id);

	public List<Project> getAllData();

	public Page<Project> getAllData(int page,int size);

}

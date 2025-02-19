package com.lead.generation.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lead.generation.entity.Project;
import com.lead.generation.repository.ProjectRepository;
import com.lead.generation.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Override
	public Project Save(Project project) throws Exception {
		try {
			project.setCreatedAt(LocalDateTime.now());
			return projectRepository.save(project);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Project update(Long id, String projectName) {
		Project projectId = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project Id " + id + "is not found"));
		if (projectName != null) {
			projectId.setProjectName(projectName);
		}
		return projectRepository.save(projectId);
	}

	@Override
	public void delete(Long id) {
		projectRepository.deleteById(id);
	}

	@Override
	public Optional<Project> getById(Long id) {
		Optional<Project> projectData = projectRepository.findById(id);
		return projectData;
	}

	@Override
	public List<Project> getAllData() {
		return projectRepository.findAll();
	}

	@Override
	public Page<Project> getAllData(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Project> Data = projectRepository.findAll(pageRequest);
		return Data;
	}

}

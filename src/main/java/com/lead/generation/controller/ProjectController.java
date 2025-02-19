package com.lead.generation.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lead.generation.entity.Project;
import com.lead.generation.service.ProjectService;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
	@Autowired
	private ProjectService projectService;

	@PostMapping("/save")
	public ResponseEntity<Map<String, String>> saveProject(@RequestBody Project project) throws Exception {
		projectService.Save(project);
		return ResponseEntity.ok(Collections.singletonMap("Message", "Project Saved successfully"));

	}

	@PutMapping("/update/{id}")
	public ResponseEntity<Map<String, String>> updateProject(@PathVariable Long id, @RequestParam String projectName) {
		projectService.update(id, projectName);
		return ResponseEntity.ok(Collections.singletonMap("Message", "Project updated successfully"));
	}

	@DeleteMapping("/delete/{Id}")
	public ResponseEntity<Map<String, String>> deleteProject(@PathVariable Long Id) {
		projectService.delete(Id);
		return ResponseEntity.ok(Collections.singletonMap("Message", "project Deleted successfully"));
	}

	@GetMapping("/getById/{Id}")
	public Optional<Project> getByID(@PathVariable Long Id) {
		Optional<Project> project = projectService.getById(Id);
		return project;
	}
	
	@GetMapping("/getAll")
	public List<Project>getAllData(){
		List<Project> project = projectService.getAllData();
		return project;
	}

	
	
	@GetMapping("/getAllprojects")
	public ResponseEntity<Page<Project>>getProjects(@RequestParam(defaultValue = "0")int page,@RequestParam int size ){
		Page<Project> project = projectService.getAllData(page,size);
		return new ResponseEntity<Page<Project>>(project,HttpStatus.OK);
	}
}

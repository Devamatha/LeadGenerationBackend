package com.lead.generation.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lead.generation.entity.AddLead;
import com.lead.generation.enumclass.LeadType;
import com.lead.generation.service.AddLeadService;

@RestController
@RequestMapping("/api/lead")
public class AddLeadController {

	@Autowired
	private AddLeadService addLeadService;

	@PostMapping("/saveLead/{clientId}")
	public ResponseEntity<Map<String, String>> saveLead(@PathVariable long clientId, @RequestParam LeadType leadType,
			@RequestParam String leadFor, @RequestParam MultipartFile image, @RequestParam String address,
			@RequestParam String bussinessType, @RequestParam String mailId, @RequestParam long mobileNumber,
			@RequestParam String bussinessName) throws IOException {

		addLeadService.saveLead(clientId, leadType, leadFor, image, address, bussinessType, mailId, mobileNumber,
				bussinessName);
		return ResponseEntity.ok(Collections.singletonMap("message", "Data saved Successfully"));

	}

	@GetMapping("/getAll-leads")
	public ResponseEntity<Page<AddLead>> getAllData(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int pageSize) {
		Page<AddLead> fetchedAllUsers = addLeadService.getAllLeadsData(page, pageSize);
		return new ResponseEntity<Page<AddLead>>(fetchedAllUsers, HttpStatus.OK);
	}

	@GetMapping("/by-leadType/{client_id}")
	public List<AddLead> getLeads(@RequestParam LeadType leadType, @PathVariable long client_id) {
		return addLeadService.getLeadsByLeadTypeAndId(leadType, client_id);
	}

	@GetMapping("/{client_id}")
	public List<AddLead> getLeadsByClientId(@PathVariable long client_id) {
		return addLeadService.getLeadsByClientId(client_id);
	}
	
	

	@GetMapping("/date/{client_id}")
	public List<AddLead> getLeadsByDateAndClientId(@PathVariable long client_id, @RequestParam LocalDate Date) {
		return addLeadService.getLeadsByCreatedDateAndId(Date, client_id);
	}

	@GetMapping("/getById/{Id}")
	public Optional<AddLead> getLeadById(@PathVariable long Id) {
		return addLeadService.getLeadsById(Id);

	}

	@PutMapping("/update/{id}")
	public AddLead updateLead(@PathVariable long id, @RequestParam(required = false) LeadType leadType,
			@RequestParam(required = false) String leadFor, @RequestParam(required = false) MultipartFile image,
			@RequestParam(required = false) String address, @RequestParam(required = false) String bussinessType,
			@RequestParam(required = false) String mailId, @RequestParam(required = false) Long mobileNumber,
			@RequestParam(required = false) String bussinessName

	) throws IOException {

		return addLeadService.updateLead(id, leadType, leadFor, image, address, bussinessType, mailId, mobileNumber,
				bussinessName);
	}

	@GetMapping("/count-per-day/{clientId}")
	public List<Map<String, Object>> getLeadsCountPerDay(@PathVariable Long clientId,
			@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year) {
		return addLeadService.getLeadsCountPerDay(clientId, month, year);
	}
	
	@GetMapping("/getleadById/{clientId}")
    public ResponseEntity<Page<AddLead>> getLeadsByClientId(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<AddLead> leads = addLeadService.getLeadsByClientId(clientId, page, size);
        return ResponseEntity.ok(leads);
    }
	
}

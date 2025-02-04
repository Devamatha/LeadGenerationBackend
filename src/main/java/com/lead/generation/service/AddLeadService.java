package com.lead.generation.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.lead.generation.entity.AddLead;
import com.lead.generation.enumclass.LeadType;

public interface AddLeadService {

	public AddLead saveLead(long clientId, LeadType leadType, String leadFor, MultipartFile image, String address,
			String bussinessType, String mailId, long mobileNumber, String bussinessName) throws IOException;

	public Page<AddLead> getAllLeadsData(int offset, int pageSize);

	public List<AddLead> getLeadsByLeadTypeAndId(LeadType leadType, long id);

	public List<AddLead> getLeadsByClientId(long id);

	public List<AddLead> getLeadsByCreatedDateAndId(LocalDate createdDate, long id);

	public Optional<AddLead> getLeadsById(long id);

	public AddLead updateLead(long id, LeadType leadType, String leadFor, MultipartFile image, String address,
			String bussinessType, String mailId, Long mobileNumber, String bussinessName) throws IOException;
    public List<Map<String, Object>> getLeadsCountPerDay(Long clientId, Integer month, Integer year) ;


}

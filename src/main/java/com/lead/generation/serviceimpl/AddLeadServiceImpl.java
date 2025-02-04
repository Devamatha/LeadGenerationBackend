package com.lead.generation.serviceimpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.lead.generation.entity.AddLead;
import com.lead.generation.entity.Clients;
import com.lead.generation.enumclass.LeadType;
import com.lead.generation.repository.AddLeadRepository;
import com.lead.generation.repository.ClientsRepository;
import com.lead.generation.service.AddLeadService;

@Service
public class AddLeadServiceImpl implements AddLeadService {
	@Autowired
	private AddLeadRepository addLeadRepository;
	@Autowired
	private ClientsRepository clientsRepository;

	@Override
	public AddLead saveLead(long clientId, LeadType leadType, String leadFor, MultipartFile image, String address,
			String bussinessType, String mailId, long mobileNumber, String bussinessName) throws IOException {
		try {
			Clients clientid = clientsRepository.findById(clientId)
					.orElseThrow(() -> new RuntimeException(clientId + " is not found"));
			AddLead addlead = new AddLead();

			if (clientid != null && clientid.getRole().equals("ROLE_EMPLOYEE")) {
				addlead.setLeadType(leadType);
				addlead.setLeadFor(leadFor);
				addlead.setImage(image.getBytes());
				addlead.setAddress(address);
				addlead.setBussinessType(bussinessType);
				addlead.setMailId(mailId);
				addlead.setMobileNumber(mobileNumber);
				addlead.setBussinessName(bussinessName);
				addlead.setCreatedAt(LocalDateTime.now());
				addlead.setCreatedDate(LocalDate.now());
				addlead.setClients(clientid);
				addLeadRepository.save(addlead);
			} else {
				throw new RuntimeException("Only Employee Need add the lead ");
			}

			return addlead;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Page<AddLead> getAllLeadsData(int offset, int pageSize) {
		PageRequest pageRequest = PageRequest.of(offset, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<AddLead> fetchAll = addLeadRepository.findAll(pageRequest);
		if (fetchAll.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.OK, "No Records Found");
		}
		return fetchAll;
	}

	@Override
	public List<AddLead> getLeadsByLeadTypeAndId(LeadType leadType, long clientId) {
		Clients clientData = clientsRepository.findById(clientId)
				.orElseThrow(() -> new RuntimeException("User " + clientId + " is not found"));

		return addLeadRepository.findByLeadTypeAndClients_Id(leadType, clientId);
	}

	@Override
	public List<AddLead> getLeadsByClientId(long id) {
		Clients clientData = clientsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User " + id + " is not found"));

		return addLeadRepository.findByClients_Id(id);
	}

	@Override
	public List<AddLead> getLeadsByCreatedDateAndId(LocalDate createdDate, long id) {
		Clients clientData = clientsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User " + id + " is not found"));

		return addLeadRepository.findByCreatedDateAndClients_Id(createdDate, id);
	}

	@Override
	public Optional<AddLead> getLeadsById(long id) {
		return addLeadRepository.findById(id);
	}

	@Override
	public AddLead updateLead(long id, LeadType leadType, String leadFor, MultipartFile image, String address,
			String bussinessType, String mailId, Long mobileNumber, String bussinessName) throws IOException {
		AddLead existingLead = addLeadRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Lead ID " + id + " not found"));

		if (leadType != null) {
			existingLead.setLeadType(leadType);
		}
		if (leadFor != null) {
			existingLead.setLeadFor(leadFor);
		}
		if (image != null && !image.isEmpty()) {
			existingLead.setImage(image.getBytes());
		}
		if (address != null) {
			existingLead.setAddress(address);
		}
		if (bussinessType != null) {
			existingLead.setBussinessType(bussinessType);
		}
		if (mailId != null) {
			existingLead.setMailId(mailId);
		}
		if (mobileNumber != null && mobileNumber > 0) {
			existingLead.setMobileNumber(mobileNumber);
		}
		if (bussinessName != null) {
			existingLead.setBussinessName(bussinessName);
		}
		return addLeadRepository.save(existingLead);
	}

	@Override
	public List<Map<String, Object>> getLeadsCountPerDay(Long clientId, Integer month, Integer year) {
	    LocalDate now = LocalDate.now();
	    if (month == null) {
	        month = now.getMonthValue();
	    }
	    if (year == null) {
	        year = now.getYear();
	    }

	    List<Object[]> results = addLeadRepository.countLeadsPerDay(clientId, year, month);

	    return results.stream().map(obj -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("date", obj[0].toString());   
	        map.put("leadCount", ((Number) obj[1]).longValue()); 
	        return map;
	    }).collect(Collectors.toList());

}
}
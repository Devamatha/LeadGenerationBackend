package com.lead.generation.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lead.generation.constants.ApplicationConstants;
import com.lead.generation.entity.AddLead;
import com.lead.generation.entity.Clients;
import com.lead.generation.record.LoginRequestDTO;
import com.lead.generation.record.LoginResponseDTO;
import com.lead.generation.repository.AddLeadRepository;
import com.lead.generation.repository.ClientsRepository;
import com.lead.generation.service.ClientsService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor

public class ClientsController {
	private final Environment env;
	private final AuthenticationManager authenticationManager;
	@Autowired
	public ClientsService clientsService;

	@Autowired
	public ClientsRepository clientsRepository;
	@Autowired
	public AddLeadRepository addLeadRepository;

	@PostMapping("/saveAdmin")
	public ResponseEntity<Clients> saveAdmin(@RequestParam String fullName, @RequestParam String email,
			@RequestParam Long mobileNumber, @RequestParam String role, @RequestParam String password) {
		Clients register = clientsService.registerAdmin(fullName, email, mobileNumber, role, password);
		return ResponseEntity.ok(register);
	}

	@PostMapping("/saveEmployee/{adminId}")
	public ResponseEntity<Clients> saveEmployee(@PathVariable long adminId, @RequestParam String fullName,
			@RequestParam String email, @RequestParam Long mobileNumber, @RequestParam String role,
			@RequestParam String password, @RequestParam MultipartFile image) {
		Clients register = clientsService.registerEmployee(fullName, email, mobileNumber, role, password, adminId,
				image);
		return ResponseEntity.ok(register);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> apiLogin(@RequestBody LoginRequestDTO loginRequest) {
		String jwt = "";
		Long id = 0L;
		String fullName = "";
		String role = "";
		Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
				loginRequest.password());

		Authentication authenticationResponse = authenticationManager.authenticate(authentication);
		List<Object[]> result = clientsRepository.findIdAndFullNameByEmail(authenticationResponse.getName());
		for (Object[] row : result) {
			id = (Long) row[0];
			fullName = (String) row[1];

		}

		role = authenticationResponse.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		if (null != authenticationResponse && authenticationResponse.isAuthenticated()) {
			if (null != env) {
				String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
						ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
				SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
				jwt = Jwts.builder().issuer("Lead Generation").subject("JWT Token")
						.claim("username", authenticationResponse.getName())
						.claim("authorities",
								authenticationResponse.getAuthorities().stream().map(GrantedAuthority::getAuthority)
										.collect(Collectors.joining(",")))
						.issuedAt(new java.util.Date())
						.expiration(new java.util.Date((new java.util.Date()).getTime() + 30000000)).signWith(secretKey)
						.compact();

			} else {
				System.out.println(env + "is not found");
			}

		} else {
			System.out.println(authenticationResponse + "is not found");
		}

		return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER, jwt)
				.body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt, id, fullName, role,loginRequest.username()));
	}

	@PostMapping("/changepassword/{Id}")
	public ResponseEntity<?> changePassword(@PathVariable Long Id, @RequestParam String password,
			@RequestParam String confirmPassword) {
		if (password != null && confirmPassword != null) {
			return clientsService.changePassword(Id, password, confirmPassword);
		} else {

			return ResponseEntity.internalServerError()
					.body(Collections.singletonMap("error", "Password is not present"));
		}
	}

	private boolean isEmail(String emailOrMobileNumber) {
		return emailOrMobileNumber.contains("@");
	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(@RequestParam String email) {
		if (email != null) {
			if (isEmail(email)) {
				return clientsService.forgotPassword(email);
			} else {

				return ResponseEntity.internalServerError()
						.body(Collections.singletonMap("error", "Invalid Email Pattern."));
			}
		} else {

			return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "Email is not present"));
		}
	}

	@GetMapping("/employees/{adminId}")
	public Map<String, Object> getEmployeesByAdminId(@PathVariable Long adminId,
			@RequestParam(defaultValue = "0") int page, @RequestParam int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<Clients> employeesPage = clientsService.getEmployeesByAdminId(adminId, pageable);

		List<Map<String, Object>> responseList = new ArrayList<>();
		for (Clients employee : employeesPage.getContent()) {
			Map<String, Object> responseData = new HashMap<>();
			if (employee.getIsActive() == true) {
				int leadCount = addLeadRepository.countLeadsByClientId(employee.getId());
				responseData.put("id", employee.getId());
				responseData.put("fullName", employee.getFullName());
				responseData.put("email", employee.getEmail());
				responseData.put("mobileNumber", employee.getMobileNumber());
				responseData.put("profileImage", employee.getImage());
				responseData.put("leadCount", leadCount);
				responseList.add(responseData);
			}

		}

		Map<String, Object> response = new HashMap<>();
		response.put("employees", responseList);
		response.put("currentPage", employeesPage.getNumber());
		response.put("totalItems", employeesPage.getTotalElements());
		response.put("totalPages", employeesPage.getTotalPages());

		return response;
	}

	
	@GetMapping("/InActive-employees/{adminId}")
	public Map<String, Object> getInAcctiveEmployeesByAdminId(@PathVariable Long adminId,
			@RequestParam(defaultValue = "0") int page, @RequestParam int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<Clients> employeesPage = clientsService.getEmployeesByAdminId(adminId, pageable);

		List<Map<String, Object>> responseList = new ArrayList<>();
		for (Clients employee : employeesPage.getContent()) {
			Map<String, Object> responseData = new HashMap<>();
			if (employee.getIsActive() == false) {
				int leadCount = addLeadRepository.countLeadsByClientId(employee.getId());
				responseData.put("id", employee.getId());
				responseData.put("fullName", employee.getFullName());
				responseData.put("email", employee.getEmail());
				responseData.put("mobileNumber", employee.getMobileNumber());
				responseData.put("profileImage", employee.getImage());
				responseData.put("leadCount", leadCount);
				responseList.add(responseData);
			}

		}

		Map<String, Object> response = new HashMap<>();
		response.put("employees", responseList);
		response.put("currentPage", employeesPage.getNumber());
		response.put("totalItems", employeesPage.getTotalElements());
		response.put("totalPages", employeesPage.getTotalPages());

		return response;
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<Clients> updateClient(@PathVariable Long id, @RequestParam(required = false) String fullName,
			@RequestParam(required = false) String email, @RequestParam(required = false) Long mobileNumber,
			@RequestParam(required = false) String role, @RequestParam(required = false) byte[] image) {

		Optional<Clients> existingClientOptional = clientsRepository.findById(id);

		if (existingClientOptional.isPresent()) {
			Clients existingClient = existingClientOptional.get();

			if (fullName != null) {
				existingClient.setFullName(fullName);
			}
			if (email != null) {
				existingClient.setEmail(email);
			}
			if (mobileNumber != null) {
				existingClient.setMobileNumber(mobileNumber);
			}
			if (role != null) {
				existingClient.setRole(role);
			}
			if (image != null) {
				existingClient.setImage(image);
			}

			Clients savedClient = clientsRepository.save(existingClient);
			return ResponseEntity.ok(savedClient);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@DeleteMapping("/delete/{Id}")
	public ResponseEntity<Map<String, String>> deleteProject(@PathVariable Long Id) {
		clientsService.delete(Id);
		return ResponseEntity.ok(Collections.singletonMap("Message", "project Deleted successfully"));
	}

}

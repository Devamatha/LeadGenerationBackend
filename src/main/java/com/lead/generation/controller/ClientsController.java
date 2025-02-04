package com.lead.generation.controller;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lead.generation.constants.ApplicationConstants;
import com.lead.generation.entity.Clients;
import com.lead.generation.record.LoginRequestDTO;
import com.lead.generation.record.LoginResponseDTO;
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

	@PostMapping("/save")
	public ResponseEntity<Clients> saveEmployeeAndAdmin(@RequestParam String fullName, @RequestParam String email,
			@RequestParam Long mobileNumber, @RequestParam String role) {
		Clients register = clientsService.registerEmployeeAndAdmin(fullName, email, mobileNumber, role);
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
				.body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt, id, fullName, role));
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
}

package com.lead.generation.serviceimpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lead.generation.entity.Clients;
import com.lead.generation.repository.ClientsRepository;
import com.lead.generation.service.ClientsService;

@Service
@Transactional

public class ClientsServiceImpl implements ClientsService {

	@Autowired
	private ClientsRepository clientsRepository;

	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("$(spring.mail.username)")
	private String fromMail;

	@Override
	public Clients registerAdmin(String fullName, String email, Long mobileNumber, String role, String password) {
		try {
			// String password = generatePassword();
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			Clients clients = new Clients();
			clients.setFullName(fullName);
			clients.setEmail(email);
			clients.setMobileNumber(mobileNumber);
			clients.setRole("ROLE_ADMIN");
			System.out.println(password + "password");
			clients.setPassword(bCryptPasswordEncoder.encode(password));
			clients.setCreatedAt(LocalDateTime.now());
			clientsRepository.save(clients);

			return clients;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public ResponseEntity<?> changePassword(Long Id, String password, String confirmPassword) {
		Clients user = clientsRepository.findById(Id)
				.orElseThrow(() -> new RuntimeException("Id is not present: " + Id));

		if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {

			user.setPassword(bCryptPasswordEncoder.encode(confirmPassword));
			clientsRepository.save(user);
			return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully."));

		} else {
			throw new RuntimeException("Incorrect current password. Please try again. ");
		}
	}

	@Override
	public ResponseEntity<?> forgotPassword(String email) {
		Optional<Clients> clients = clientsRepository.findByEmail(email);

		if (clients != null) {
			Clients user = clients.get();
			String password = generatePassword();
			user.setPassword(bCryptPasswordEncoder.encode(password));
			clientsRepository.save(user);
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setFrom(fromMail);
			simpleMailMessage.setTo(email);
			simpleMailMessage
					.setSubject("New Password Generation completed Successfully in LeadsGeneration application\n");
			simpleMailMessage.setText("Dear " + user.getFullName()
					+ "\n\nPlease check your  email and generted password  \n UserEmail  :" + email
					+ "\n  MobileNumber :" + user.getMobileNumber() + "\n New Password   :" + password + "\n\n"
					+ "you will be required to reset the New password upon login\n\n\n if you have any question or if you would like to request a call-back,please email us at support info@techpixe.com");
			javaMailSender.send(simpleMailMessage);
			return ResponseEntity
					.ok(Collections.singletonMap("message", "Password send to " + email + " successfully."));
		} else {
			throw new RuntimeException("Incorrect email password. Please try again. " + email);

		}
	}

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DIGITS = "0123456789";

	public static String generatePassword() {
		Random random = new Random();

		StringBuilder lettersBuilder = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			int index = random.nextInt(LETTERS.length());
			lettersBuilder.append(LETTERS.charAt(index));
		}

		StringBuilder digitsBuilder = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			int index = random.nextInt(DIGITS.length());
			digitsBuilder.append(DIGITS.charAt(index));
		}

		return lettersBuilder.toString() + digitsBuilder.toString();
	}

	@Override
	public Clients registerEmployee(String fullName, String email, Long mobileNumber, String role, String password,
			long adminId, MultipartFile image) {
		try {
			Clients admin = clientsRepository.findById(adminId)
					.orElseThrow(() -> new RuntimeException(adminId + " admin is not found"));
			Clients clients = new Clients();

			if (admin.getRole().equals("ROLE_ADMIN")) {
				BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
				clients.setFullName(fullName);
				clients.setEmail(email);
				clients.setMobileNumber(mobileNumber);
				clients.setRole("ROLE_EMPLOYEE");
				clients.setPassword(bCryptPasswordEncoder.encode(password));
				clients.setCreatedAt(LocalDateTime.now());
				clients.setAdminId(adminId);
				clients.setImage(image.getBytes());
				clientsRepository.save(clients);
			}

			return clients;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Clients> getEmployeesByAdminId(Long adminId) {
		return clientsRepository.findByAdminIdAndRole(adminId, "ROLE_EMPLOYEE");
	}

	@Override

	public Page<Clients> getEmployeesByAdminId(Long adminId, Pageable pageable) {
		return clientsRepository.findByAdminId(adminId, pageable);
	}

	@Override
	public void delete(Long id) {
		clientsRepository.softDelete(id);
	}
}

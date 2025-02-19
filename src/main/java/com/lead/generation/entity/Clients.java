package com.lead.generation.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Table(name = "clients")
public class Clients {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fullName;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false, unique = true)
	private Long mobileNumber;
	@JsonIgnore
	private String password;
	@JsonIgnore
	private LocalDateTime createdAt;
	private String role;
	private long adminId;
	private Boolean isActive = true;
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] image;
	@JsonIgnore
	@JsonManagedReference
	@OneToMany(mappedBy = "clients", fetch = FetchType.EAGER)
	private List<AddLead> clients = new ArrayList<>();

}

package com.lead.generation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;

@Data
public class RegisterDto {
	// ClientTable
	@JsonIgnore
	private Long id;
	private String fullName;
	private String email;
	private Long mobileNumber;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private LocalDate createdAt;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String role;

}

package com.lead.generation.service;

import org.springframework.http.ResponseEntity;

import com.lead.generation.entity.Clients;

public interface ClientsService {


    public Clients registerEmployeeAndAdmin(String fullName, String email, Long mobileNumber, String role);
    public ResponseEntity<?> changePassword(Long Id, String password, String confirmPassword) ;
    public ResponseEntity<?> forgotPassword(String email);
    

}

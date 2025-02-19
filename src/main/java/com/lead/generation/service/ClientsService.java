package com.lead.generation.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.lead.generation.entity.Clients;

public interface ClientsService {


    public Clients registerAdmin(String fullName, String email, Long mobileNumber, String role,String password);
    public Clients registerEmployee(String fullName, String email, Long mobileNumber, String role,String password,long adminId,MultipartFile image);

    public ResponseEntity<?> changePassword(Long Id, String password, String confirmPassword) ;
    public ResponseEntity<?> forgotPassword(String email);
    
    public List<Clients> getEmployeesByAdminId(Long adminId) ;
    
	public Page<Clients> getEmployeesByAdminId(Long adminId, Pageable pageable) ;

    
	public void delete(Long id) ;
	
}

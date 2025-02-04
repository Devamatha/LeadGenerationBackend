package com.lead.generation.record;

public record LoginResponseDTO(String status, String jwtToken,Long id,String fullName,String role) {
}


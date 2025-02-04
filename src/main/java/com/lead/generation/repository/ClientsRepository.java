package com.lead.generation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lead.generation.entity.Clients;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Long> {
    Optional<Clients> findByEmail(String email);

    @Query("SELECT c.id, c.fullName FROM Clients c WHERE c.email = :email")
    List<Object[]> findIdAndFullNameByEmail(@Param("email") String email);
    


    @Query("SELECT c.password, c.role FROM Clients c WHERE c.email = :email")
    List<Object[]> findPasswordAndRoleByEmail(@Param("email") String email);
}

package com.lead.generation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lead.generation.entity.Clients;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Long> {
	Optional<Clients> findByEmail(String email);

	@Query("SELECT c.id, c.fullName FROM Clients c WHERE c.email = :email")
	List<Object[]> findIdAndFullNameByEmail(@Param("email") String email);

	@Query("SELECT c.password, c.role FROM Clients c WHERE c.email = :email AND c.isActive=true")
	List<Object[]> findPasswordAndRoleByEmailAndIsActiveTrue(@Param("email") String email);

	List<Clients> findByAdminIdAndRole(Long adminId, String role);

	Page<Clients> findByAdminId(Long adminId, Pageable pageable);

	@Modifying
	@Transactional

	@Query("UPDATE Clients c SET c.isActive = false WHERE c.id = :id")
	void softDelete(@Param("id") Long id);

}

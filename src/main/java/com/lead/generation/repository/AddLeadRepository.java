package com.lead.generation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lead.generation.entity.AddLead;
import com.lead.generation.enumclass.LeadType;

public interface AddLeadRepository extends JpaRepository<AddLead, Long> {
	List<AddLead> findByLeadTypeAndClients_Id(LeadType leadType, long clientId);

	List<AddLead> findByCreatedDateAndClients_Id(LocalDate createdDate, long clientId);

	List<AddLead> findByClients_Id(long clientId);

	@Query("SELECT al.createdDate, COUNT(al) " + "FROM AddLead al " + "WHERE al.clients.id = :clientId "
			+ "AND FUNCTION('YEAR', al.createdDate) = :year " + "AND FUNCTION('MONTH', al.createdDate) = :month "
			+ "GROUP BY al.createdDate " + "ORDER BY al.createdDate")
	List<Object[]> countLeadsPerDay(@Param("clientId") Long clientId, @Param("year") int year,
			@Param("month") int month);

	@Query("SELECT COUNT(a) FROM AddLead a WHERE a.clients.id = :clientId")
	int countLeadsByClientId(Long clientId);
    Page<AddLead> findByClientsId(Long clientId, Pageable pageable);

}

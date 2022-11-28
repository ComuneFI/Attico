package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.linksmt.assatti.datalayer.domain.RicevutaPec;

/**
 * Spring Data JPA repository for the RicevutaPec entity.
 */
public interface RicevutaPecRepository extends JpaRepository<RicevutaPec, Long> {
	
	@Query(value="select count(*) from ricevutapec where messageid = ?1", nativeQuery=true)
	public Long countByMessageId(String messageId);
	
}

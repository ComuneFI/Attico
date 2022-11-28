package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.datalayer.domain.StatoJob;

/**
 * Spring Data JPA repository for the JobPubblicazione entity.
 */
public interface JobPubblicazioneRepository extends JpaRepository<JobPubblicazione,Long>, QueryDslPredicateExecutor<JobPubblicazione> {
	List<JobPubblicazione> findByStatoIn(List<StatoJob> stati);
	
	@Modifying
	@Query(value="INSERT INTO job_pubblicazione(created_date, created_by, modified_by, modified_date, stato, atto_id, tentativi, last_modified_by, last_modified_date, version) VALUES(now(), ?1, ?1, now(), ?2, ?3, 0, ?1, now(), 0)", nativeQuery=true)
	void richiediNuovaPubblicazione(String createdBy, String stato, Long attoId);
	
	@Modifying
	@Query(value="update job_pubblicazione set modified_by=?3, modified_date=now(), stato = ?2, tentativi=?4, last_modified_date=now(), version=4 where id = ?1", nativeQuery=true)
	void updateStato(Long id, String stato, String modifiedBy, Integer tentativi);
	
	@Modifying
	@Query(value="update job_pubblicazione set modified_by=?3, modified_date=now(), stato = ?2, tentativi=?4, last_modified_date=now(), version=4, dettaglio_errore = ?5  where id = ?1", nativeQuery=true)
	void updateStatoAndErrore(Long id, String stato, String modifiedBy, Integer tentativi, String errore);
	
	
	@Query(value="select dettaglio_errore from job_pubblicazione where atto_id = ?;", nativeQuery = true)
	public List<String> getDettaglioErroreByAttoId(Long attoId);
	
	public List<JobPubblicazione> findByAttoId(Long attoId);
}

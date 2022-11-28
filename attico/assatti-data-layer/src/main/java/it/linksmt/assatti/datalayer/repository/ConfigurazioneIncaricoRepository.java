package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncarico;

/**
 * Spring Data JPA repository for the ConfigurazioneIncarico entity.
 */
public interface ConfigurazioneIncaricoRepository extends JpaRepository<ConfigurazioneIncarico, Long>, QueryDslPredicateExecutor<ConfigurazioneIncarico> {
	
	@Query(value="SELECT confIncarico FROM ConfigurazioneIncarico confIncarico "
			+ " WHERE confIncarico.configurazioneTask.codice = ?1 AND confIncarico.atto.id = ?2 "
			+ " ORDER BY confIncarico.dataModifica DESC")
	List<ConfigurazioneIncarico> findLastByCodiceAndAtto_Id(String codice, long idAtto);
}

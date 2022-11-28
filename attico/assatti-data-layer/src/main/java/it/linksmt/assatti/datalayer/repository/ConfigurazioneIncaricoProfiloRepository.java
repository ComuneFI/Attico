package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfiloId;

/**
 * Spring Data JPA repository for the ConfigurazioneIncaricoProfilo entity.
 */
public interface ConfigurazioneIncaricoProfiloRepository extends JpaRepository<ConfigurazioneIncaricoProfilo, ConfigurazioneIncaricoProfiloId>, QueryDslPredicateExecutor<ConfigurazioneIncaricoProfilo> {

	// @Query(value = "SELECT * FROM configurazione_incarico_profilo WHERE id_configurazione_incarico = ?1", nativeQuery = true)
	List<ConfigurazioneIncaricoProfilo> findByPrimaryKey_IdConfigurazioneIncarico(Long idConfigurazioneIncarico);
	
	void deleteByPrimaryKey_IdConfigurazioneIncarico(Long idConfigurazioneIncarico);
	
	@Query(value="select count(*) from configurazione_incarico_profilo cip inner join configurazione_incarico ci on cip.id_configurazione_incarico = ci.id inner join atto on ci.atto_id = atto.id where cip.id_profilo = ?1 and atto.fineiter_date is null", nativeQuery=true)
	public BigInteger countConfIncaricoByProfiloAttiAttivi(Long profiloId);

}

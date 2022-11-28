package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAooId;

/**
 * Spring Data JPA repository for the ConfigurazioneIncaricoAoo entity.
 */
public interface ConfigurazioneIncaricoAooRepository extends JpaRepository<ConfigurazioneIncaricoAoo, ConfigurazioneIncaricoAooId>, QueryDslPredicateExecutor<ConfigurazioneIncaricoAoo> {

	@Query(value = "SELECT * FROM configurazione_incarico_aoo WHERE id_configurazione_incarico = ?1", nativeQuery = true)
	List<ConfigurazioneIncaricoAoo> findByConfigurazioneIncarico(Long idConfigurazioneIncarico);
	
	void deleteByPrimaryKey_IdConfigurazioneIncarico(Long idConfigurazioneIncarico);

}

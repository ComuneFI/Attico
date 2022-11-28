package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneTask;

/**
 * Spring Data JPA repository for the ConfigurazioneTask entity.
 */
public interface ConfigurazioneTaskRepository extends JpaRepository<ConfigurazioneTask, Long>, QueryDslPredicateExecutor<ConfigurazioneTask>{

	ConfigurazioneTask findByCodice(String codice);

}

package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskAooId;

/**
 * Spring Data JPA repository for the ConfigurazioneTaskAoo entity.
 */
public interface ConfigurazioneTaskAooRepository extends JpaRepository<ConfigurazioneTaskAoo, ConfigurazioneTaskAooId> {

	@Query(value = "SELECT * FROM configurazione_task_aoo WHERE id_configurazione_task = ?1", nativeQuery = true)
	List<ConfigurazioneTaskAoo> findByConfigurazioneTask(Long idConfigurazioneTask);
	
	void deleteByPrimaryKey_IdConfigurazioneTask(Long idConfigurazioneTask);

}

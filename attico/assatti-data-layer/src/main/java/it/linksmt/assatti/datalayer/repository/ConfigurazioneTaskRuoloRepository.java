package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskRuolo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskRuoloId;

/**
 * Spring Data JPA repository for the ConfigurazioneTaskRuolo entity.
 */
public interface ConfigurazioneTaskRuoloRepository extends JpaRepository<ConfigurazioneTaskRuolo, ConfigurazioneTaskRuoloId> {

	@Query(value = "SELECT * FROM configurazione_task_ruolo WHERE id_configurazione_task = ?1", nativeQuery = true)
	List<ConfigurazioneTaskRuolo> findByConfigurazioneTask(Long idConfigurazioneTask);
	
	void deleteByPrimaryKey_IdConfigurazioneTask(Long idConfigurazioneTask);

}

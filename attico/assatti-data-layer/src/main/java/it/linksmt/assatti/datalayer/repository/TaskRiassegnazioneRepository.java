package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TaskRiassegnazione;

/**
 * Spring Data JPA repository for the TaskRiassegnazione entity.
 */
public interface TaskRiassegnazioneRepository extends JpaRepository<TaskRiassegnazione,Long>, QueryDslPredicateExecutor<TaskRiassegnazione> {

}

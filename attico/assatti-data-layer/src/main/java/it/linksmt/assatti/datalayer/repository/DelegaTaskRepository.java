package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.DelegaTask;

/**
 * Spring Data JPA repository for the DelegaTask entity.
 */
public interface DelegaTaskRepository extends JpaRepository<DelegaTask, Long>, QueryDslPredicateExecutor<DelegaTask>{

	
}

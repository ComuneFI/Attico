package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.SchedulerChecking;

/**
 * Spring Data JPA repository for the SchedulerChecking entity.
 */
public interface SchedulerCheckingRepository extends JpaRepository<SchedulerChecking, Long>,QueryDslPredicateExecutor<SchedulerChecking> {
	
}

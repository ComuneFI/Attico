package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.RuntimeOperation;

/**
 * Spring Data JPA repository for the RuntimeOperation entity.
 */
public interface RuntimeOperationRepository extends JpaRepository<RuntimeOperation,Long>,QueryDslPredicateExecutor<RuntimeOperation> {

}

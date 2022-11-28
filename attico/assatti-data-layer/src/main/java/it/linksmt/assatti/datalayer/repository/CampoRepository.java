package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Campo;

/**
 * Spring Data JPA repository for the Campo entity.
 */
public interface CampoRepository extends JpaRepository<Campo,Long>, QueryDslPredicateExecutor<Campo> {

}

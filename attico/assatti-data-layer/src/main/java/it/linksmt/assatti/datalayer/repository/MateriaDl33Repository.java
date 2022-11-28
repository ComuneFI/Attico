package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.MateriaDl33;

/**
 * Spring Data JPA repository for the MateriaDl33 entity.
 */
public interface MateriaDl33Repository extends JpaRepository<MateriaDl33,Long>, QueryDslPredicateExecutor<MateriaDl33> {

}

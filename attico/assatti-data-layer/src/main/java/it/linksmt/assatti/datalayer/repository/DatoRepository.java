package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Dato;

/**
 * Spring Data JPA repository for the Dato entity.
 */
public interface DatoRepository extends JpaRepository<Dato,Long>, QueryDslPredicateExecutor<Dato> {

}

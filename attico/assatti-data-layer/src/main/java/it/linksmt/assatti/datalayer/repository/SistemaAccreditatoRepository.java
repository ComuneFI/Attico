package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.SistemaAccreditato;

/**
 * Spring Data JPA repository for the SistemaAccreditato entity.
 */
public interface SistemaAccreditatoRepository extends JpaRepository<SistemaAccreditato,Long>, QueryDslPredicateExecutor<SistemaAccreditato> {

}

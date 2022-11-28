package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Indirizzo;

/**
 * Spring Data JPA repository for the Indirizzo entity.
 */
public interface IndirizzoRepository extends JpaRepository<Indirizzo,Long>, QueryDslPredicateExecutor<Indirizzo> {

}

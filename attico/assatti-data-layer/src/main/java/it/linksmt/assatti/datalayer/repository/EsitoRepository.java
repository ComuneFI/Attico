package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Esito;

/**
 * Spring Data JPA repository for the Esito entity.
 */
public interface EsitoRepository extends JpaRepository<Esito,String>,QueryDslPredicateExecutor<Esito> {
	Esito findById(String id);
}

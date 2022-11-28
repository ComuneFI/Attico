package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoEvento;

/**
 * Spring Data JPA repository for the TipoEvento entity.
 */
public interface TipoEventoRepository extends JpaRepository<TipoEvento,Long>, QueryDslPredicateExecutor<TipoEvento> {

	TipoEvento findByCodice(String codice);

}

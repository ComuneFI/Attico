package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoOdg;

/**
 * Spring Data JPA repository for the TipoOdg entity.
 */
public interface TipoOdgRepository extends JpaRepository<TipoOdg,Long>, QueryDslPredicateExecutor<TipoOdg> {

}

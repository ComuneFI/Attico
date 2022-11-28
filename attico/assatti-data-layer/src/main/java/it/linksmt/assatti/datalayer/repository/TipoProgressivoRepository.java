package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoProgressivo;

/**
 * Spring Data JPA repository for the TipoProgressivo entity.
 */
public interface TipoProgressivoRepository extends JpaRepository<TipoProgressivo,Long>, QueryDslPredicateExecutor<TipoProgressivo> {

}

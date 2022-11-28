package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoAdempimento;

/**
 * Spring Data JPA repository for the TipoAdempimento entity.
 */
public interface TipoAdempimentoRepository extends JpaRepository<TipoAdempimento,Long>, QueryDslPredicateExecutor<TipoAdempimento> {

}

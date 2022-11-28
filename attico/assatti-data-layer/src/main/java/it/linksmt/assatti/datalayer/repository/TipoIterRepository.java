package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoIter;

/**
 * Spring Data JPA repository for the TipoIter entity.
 */
public interface TipoIterRepository extends JpaRepository<TipoIter,Long>, QueryDslPredicateExecutor<TipoIter> {

	List<TipoIter> findByTipoAttoId(Long tipoAttoId);
}

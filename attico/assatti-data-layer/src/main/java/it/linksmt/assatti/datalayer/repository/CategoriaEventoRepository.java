package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.CategoriaEvento;

/**
 * Spring Data JPA repository for the CategoriaEvento entity.
 */
public interface CategoriaEventoRepository extends JpaRepository<CategoriaEvento,Long>, QueryDslPredicateExecutor<CategoriaEvento> {

	 public List<CategoriaEvento> findAllByOrderByOrdineAsc();
}

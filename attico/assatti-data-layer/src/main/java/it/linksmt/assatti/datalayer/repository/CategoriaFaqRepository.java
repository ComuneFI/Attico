package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.CategoriaFaq;

/**
 * Spring Data JPA repository for the CategoriaFaq entity.
 */
public interface CategoriaFaqRepository extends JpaRepository<CategoriaFaq,Long>, QueryDslPredicateExecutor<CategoriaFaq> {

}

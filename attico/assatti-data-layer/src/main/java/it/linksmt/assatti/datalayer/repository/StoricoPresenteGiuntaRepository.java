package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.StoricoPresenteGiunta;

/**
 * Spring Data JPA repository for the Storico Atto Giunta entity.
 */
public interface StoricoPresenteGiuntaRepository extends JpaRepository<StoricoPresenteGiunta,Long>,QueryDslPredicateExecutor<StoricoPresenteGiunta>{
}

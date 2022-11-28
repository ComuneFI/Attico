package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.StoricoAssenteGiunta;

/**
 * Spring Data JPA repository for the Storico Assente Giunta entity.
 */
public interface StoricoAssenteGiuntaRepository extends JpaRepository<StoricoAssenteGiunta,Long>,QueryDslPredicateExecutor<StoricoAssenteGiunta>{
}

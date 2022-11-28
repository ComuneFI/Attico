package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoDeterminazione;

/**
 * Spring Data JPA repository for the TipoDeterminazione entity.
 */
public interface TipoDeterminazioneRepository extends JpaRepository<TipoDeterminazione,Long>, QueryDslPredicateExecutor<TipoDeterminazione> {

}

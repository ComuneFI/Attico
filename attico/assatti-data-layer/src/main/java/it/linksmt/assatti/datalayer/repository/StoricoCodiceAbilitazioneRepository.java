package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.StoricoCodiceAbilitazione;

/**
 * Spring Data JPA repository for the Storico Codice Abilitazione entity.
 */
public interface StoricoCodiceAbilitazioneRepository extends JpaRepository<StoricoCodiceAbilitazione,Long>,QueryDslPredicateExecutor<StoricoCodiceAbilitazione>{
}

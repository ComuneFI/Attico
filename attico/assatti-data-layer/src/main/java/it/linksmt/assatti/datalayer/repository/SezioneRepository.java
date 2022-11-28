package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Sezione;

/**
 * Spring Data JPA repository for the Sezione entity.
 */
public interface SezioneRepository extends JpaRepository<Sezione,Long>, QueryDslPredicateExecutor<Sezione> {

}

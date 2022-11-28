package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.AmbitoDl33;

/**
 * Spring Data JPA repository for the AmbitoDl33 entity.
 */
public interface AmbitoDl33Repository extends JpaRepository<AmbitoDl33,Long>, QueryDslPredicateExecutor<AmbitoDl33> {

	List<AmbitoDl33> findAllByOrderByDenominazioneAsc();

}

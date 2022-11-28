package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ProfiloComposizione;

/**
 * Spring Data JPA repository for the ProfiloComposizione entity.
 */
public interface ProfiloComposizioneRepository extends JpaRepository<ProfiloComposizione,Long>, QueryDslPredicateExecutor<ProfiloComposizione> {

;
}

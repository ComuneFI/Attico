package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.RubricaDestinatarioEsterno;

/**
 * Spring Data JPA repository for the RubricaDestinatarioEsterno entity.
 */
public interface RubricaDestinatarioEsternoRepository extends
		JpaRepository<RubricaDestinatarioEsterno, Long>,
		QueryDslPredicateExecutor<RubricaDestinatarioEsterno> {

	Page<RubricaDestinatarioEsterno> findByAooId(Long aooId, Pageable page);

}

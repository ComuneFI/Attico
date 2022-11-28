package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.RubricaBeneficiario;

/**
 * Spring Data JPA repository for the RubricaBeneficiario entity.
 */
public interface RubricaBeneficiarioRepository extends
		JpaRepository<RubricaBeneficiario, Long>,
		QueryDslPredicateExecutor<RubricaBeneficiario> {

	Page<RubricaBeneficiario> findByAooId(Long aooId, Pageable page);

}

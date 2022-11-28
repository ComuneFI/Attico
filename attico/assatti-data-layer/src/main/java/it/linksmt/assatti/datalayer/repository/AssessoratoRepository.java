package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Assessorato;

/**
 * Spring Data JPA repository for the Assessorato entity.
 */
public interface AssessoratoRepository extends JpaRepository<Assessorato,Long>, QueryDslPredicateExecutor<Assessorato> {
	
}

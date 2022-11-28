package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Ufficio;

/**
 * Spring Data JPA repository for the Ufficio entity.
 */
public interface UfficioRepository extends JpaRepository<Ufficio,Long>,QueryDslPredicateExecutor<Ufficio>  {

	List<Ufficio> findByAooId(Long aooId);

}

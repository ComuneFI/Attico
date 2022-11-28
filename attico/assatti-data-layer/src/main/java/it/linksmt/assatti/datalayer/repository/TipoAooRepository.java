package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.TipoAoo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Spring Data JPA repository for the TipoAoo entity.
 */
public interface TipoAooRepository extends JpaRepository<TipoAoo,Long>,QueryDslPredicateExecutor<TipoAoo> {

	
}

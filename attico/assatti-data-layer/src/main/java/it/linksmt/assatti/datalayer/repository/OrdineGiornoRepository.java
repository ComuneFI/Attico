package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.OrdineGiorno;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Spring Data JPA repository for the OrdineGiorno entity.
 */
public interface OrdineGiornoRepository extends JpaRepository<OrdineGiorno,Long>,QueryDslPredicateExecutor<OrdineGiorno> {

	@Query("select distinct odg from OrdineGiorno odg left join fetch odg.attos where odg.sedutaGiunta.id = ?1")
	List<OrdineGiorno> findBySeduta(Long seduta);
}

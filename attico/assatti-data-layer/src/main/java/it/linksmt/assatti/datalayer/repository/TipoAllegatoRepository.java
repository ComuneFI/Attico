package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoAllegato;

/**
 * Spring Data JPA repository for the TipoAllegato entity.
 */
public interface TipoAllegatoRepository extends JpaRepository<TipoAllegato, Long>, QueryDslPredicateExecutor<TipoAllegato> {
	
	public TipoAllegato findByCodice(String codice);

}

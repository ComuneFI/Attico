package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerie;

/**
 * Spring Data JPA repository for the TipoDocumento entity.
 */
public interface TipoDocumentoSerieRepository extends JpaRepository<TipoDocumentoSerie,Long>, QueryDslPredicateExecutor<TipoDocumentoSerie> {
	
	public TipoDocumentoSerie findByCodice(String codice);
}

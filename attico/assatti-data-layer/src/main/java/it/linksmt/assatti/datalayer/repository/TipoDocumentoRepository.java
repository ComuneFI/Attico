package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoDocumento;

/**
 * Spring Data JPA repository for the TipoDocumento entity.
 */
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento,Long>, QueryDslPredicateExecutor<TipoDocumento> {
	public TipoDocumento findByCodice(String codice);
	
	@Query(value = "select count(*) from tipodocumento where codice = ?1", nativeQuery = true)
	public Integer countTipoDocumentoByCodice(String codice);
	
	@Query(value = "select count(*) from tipodocumento where codice = ?1 and id <> ?2", nativeQuery = true)
	public Integer countTipoDocumentoByCodiceButId(String codice, Long id);
}

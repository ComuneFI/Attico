package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;

/**
 * Spring Data JPA repository for the ModelloHtml entity.
 */
public interface ModelloHtmlRepository extends JpaRepository<ModelloHtml,Long>, QueryDslPredicateExecutor<ModelloHtml> {
	public List<ModelloHtml> findAllByTipoDocumento(TipoDocumento tipoDocumento); 
	
	public List<ModelloHtml> findAllByTitolo(String titolo);
}

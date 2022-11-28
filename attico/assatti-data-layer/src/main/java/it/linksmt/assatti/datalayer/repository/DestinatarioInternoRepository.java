package it.linksmt.assatti.datalayer.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.AttoHasDestinatario;

/**
 * Spring Data JPA repository for the AttoHasDestinatario.
 */
public interface DestinatarioInternoRepository extends JpaRepository<AttoHasDestinatario,Long>,QueryDslPredicateExecutor<AttoHasDestinatario>  {
	public Set<AttoHasDestinatario> findByAttoId(Long attoId);
	public Set<AttoHasDestinatario> findBySedutaId(Long sedutaId);
	public Set<AttoHasDestinatario> findByDocumentoPdfId(Long documentoId);
	public void deleteByAttoId(Long attoId);
	public void deleteBySedutaId(Long sedutaId);
	
	public AttoHasDestinatario findByDestinatarioIdAndDocumentoPdfId(Long destinatarioId, Long documentoId);
}

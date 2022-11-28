package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoDestinatario;

/**
 * Spring Data JPA repository for the AttoHasDestinatario.
 */
public interface TipoDestinatarioRepository extends JpaRepository<TipoDestinatario,Long>,QueryDslPredicateExecutor<TipoDestinatario>  {
	public TipoDestinatario findByDescrizione(String descrizione);
}

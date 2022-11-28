package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.StoricoDocumento;

/**
 * Spring Data JPA repository for the Storico Documento entity.
 */
public interface StoricoDocumentoRepository extends JpaRepository<StoricoDocumento,Long>,QueryDslPredicateExecutor<StoricoDocumento>{
}

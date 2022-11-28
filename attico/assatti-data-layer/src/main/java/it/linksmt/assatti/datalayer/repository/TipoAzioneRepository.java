package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoAzione;

public interface TipoAzioneRepository extends JpaRepository<TipoAzione,String>,QueryDslPredicateExecutor<TipoAzione> {
	// Esito findById(String id);
}

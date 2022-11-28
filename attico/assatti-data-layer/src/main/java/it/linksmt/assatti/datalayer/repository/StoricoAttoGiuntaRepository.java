package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.StoricoAttoGiunta;

/**
 * Spring Data JPA repository for the Storico Atto Giunta entity.
 */
public interface StoricoAttoGiuntaRepository extends JpaRepository<StoricoAttoGiunta,Long>,QueryDslPredicateExecutor<StoricoAttoGiunta>{
	public StoricoAttoGiunta findByCodiceCifra(String codiceCifra);
}

package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.StoricoAttoDirigenziale;

/**
 * Spring Data JPA repository for the Storico Atto Dirigenziale entity.
 */
public interface StoricoAttoDirigenzialeRepository extends JpaRepository<StoricoAttoDirigenziale,Long>,QueryDslPredicateExecutor<StoricoAttoDirigenziale>{
	public StoricoAttoDirigenziale findByCodiceCifra(String codiceCifra);
}

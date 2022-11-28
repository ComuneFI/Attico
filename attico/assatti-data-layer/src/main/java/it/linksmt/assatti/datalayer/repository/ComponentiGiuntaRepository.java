package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;


/**
 * Spring Data JPA repository for the SottoscrittoreAtto entity.
 */
public interface ComponentiGiuntaRepository extends JpaRepository<ComponentiGiunta,Long>, QueryDslPredicateExecutor<ComponentiGiunta> {

	public List<ComponentiGiunta> findByOrdineGiorno(OrdineGiorno odg);
	public List<ComponentiGiunta> findByAtto(AttiOdg atto);
}

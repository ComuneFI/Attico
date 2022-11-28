package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;


/**
 * Spring Data JPA repository for the SottoscrittoreAtto entity.
 */
public interface AttiOdgRepository extends JpaRepository<AttiOdg,Long>, QueryDslPredicateExecutor<AttiOdg>, AttiOdgRepositoryCustom {

	public List<AttiOdg> findByOrdineGiorno(OrdineGiorno odg);
	public List<AttiOdg> findByAtto(Atto atto);
	
	/*
	@Modifying
	@Query(value="update attiodg set blocco_modifica = 1 where atto_id = ?1", nativeQuery = true)
	public void bloccoModificaVotazioni(Long attoId);
	*/
	
	@Query(value="select distinct attiOdg.atto from AttiOdg attiOdg where attiOdg.ordineGiorno.sedutaGiunta.id = ?1 "
			+ " and attiOdg.esito not in ?2 order by attiOdg.numeroDiscussione")
	public List<Atto> findAttoBySedutaGiuntaAndEsitoNotInOrderByDiscussione(Long sedutaId, List<String> esiti);
	
	@Query(value="select attiOdg.numeroArgomento from AttiOdg attiOdg where attiOdg.atto.id= ?1 AND attiOdg.numeroArgomento > 0 and attiOdg.id <> ?2 ORDER BY attiOdg.id DESC")
	public List<Integer> getLastNumeriArgomentoAtto(long idAtto, long attoOdgId);
}

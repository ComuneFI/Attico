package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.EsitoPareri;

/**
 * Spring Data JPA repository for the EsitoPareri entity.
 */
public interface EsitoPareriRepository extends JpaRepository<EsitoPareri,Long>,QueryDslPredicateExecutor<EsitoPareri> {
	List<EsitoPareri> findByCodice(String codice);
	
	@Query("SELECT e from EsitoPareri e where e.codice like %?1% ")
	List<EsitoPareri> findByLikeCodice(String codice);
	
	@Query(value="select valore from esitoPareri where codice = ?1", nativeQuery=true)
	String getValoreByCodiceEsitoPareri(String codiceRuolo);
	
	@Modifying
	@Query(value="update esitoPareri set enabled = ?1 where id = ?2", nativeQuery=true)
	public void enableDisable(boolean enabled, Long idEsitoPareri);

}

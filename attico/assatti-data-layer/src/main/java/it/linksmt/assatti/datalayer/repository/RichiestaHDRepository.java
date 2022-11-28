package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.RichiestaHD;

/**
 * Spring Data JPA repository for the RichiestaHD entity.
 */
public interface RichiestaHDRepository extends JpaRepository<RichiestaHD,Long>, QueryDslPredicateExecutor<RichiestaHD>  {
	@Modifying
	@Query(value="update richiestahd set stato_id = ?1 where id = ?2", nativeQuery=true)
	public void updateStatoRichiestaHD(Long statoId, Long richiestaId);
	
	@Modifying
	@Query(value="update richiestahd set data_presa_visione_op = ?1 where data_presa_visione_op is null and id = ?2", nativeQuery=true)
	public void presaVisione(String data, Long richiestaId);
	
	@Modifying
	@Query(value="update richiestahd set data_chiusura = ?1 where id = ?2", nativeQuery=true)
	public void registraDataChiusura(String data, Long richiestaId);
	
	@Modifying
	@Query(value="update richiestahd set data_sospensione = ?1 where id = ?2", nativeQuery=true)
	public void registraDataSospensione(String data, Long richiestaId);
	
	@Modifying
	@Query(value="update richiestahd set data_sospensione = null, data_chiusura = null where id = ?1", nativeQuery=true)
	public void nullToDataSospensioneChiusura(Long richiestaId);
	
	@Modifying
	@Query(value="update richiestahd set testo_richiesta = ?1 where id = ?2", nativeQuery=true)
	public void updateTestoRichiesta(String testoRichiesta, Long richiestaId);
}

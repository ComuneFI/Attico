package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.SottoscrittoreSedutaGiunta;

/**
 * Spring Data JPA repository for the SottoscrittoreSedutaGiunta entity.
 */
public interface SottoscrittoreSedutaGiuntaRepository extends JpaRepository<SottoscrittoreSedutaGiunta,Long>, QueryDslPredicateExecutor<SottoscrittoreSedutaGiunta> {

	@Query("SELECT s FROM SottoscrittoreSedutaGiunta s "
			+ "WHERE s.verbale.id =:idverbale AND s.profilo.id =:idprofilo")
	SottoscrittoreSedutaGiunta findByVerbaleAndProfilo(@Param("idverbale") Long idVerbale, @Param("idprofilo") Long idProfilo);
	
	@Query("SELECT s FROM SottoscrittoreSedutaGiunta s "
			+ "WHERE s.verbale.id =:idverbale "
			+ "ORDER BY s.ordineFirma ASC ")
    List<SottoscrittoreSedutaGiunta> findByVerbaleOrderByOrdineFirmaAsc(@Param("idverbale") Long idVerbale);
	
	@Query("SELECT s From SottoscrittoreSedutaGiunta s "
			+ "WHERE s.verbale.id =:idverbale AND s.firmato =:firmato "
			+ "ORDER BY s.ordineFirma ASC ")
    List<SottoscrittoreSedutaGiunta> findByVerbaleAndFirmatoOrderByOrdineFirmaAsc(@Param("idverbale") Long idVerbale,@Param("firmato") boolean firmato);

	@Query("SELECT s FROM SottoscrittoreSedutaGiunta s "
			+ "WHERE s.sedutaresoconto.id =:idseduta AND s.ordineFirma = 1")
	SottoscrittoreSedutaGiunta findFirstBySedutaResoconto(@Param("idseduta") Long idSeduta);
	
	@Query("SELECT s FROM SottoscrittoreSedutaGiunta s "
			+ "WHERE s.sedutaresoconto.id =:idseduta AND s.ordineFirma =:ordine")
	SottoscrittoreSedutaGiunta findBySedutaResocontoAndOrdine(@Param("idseduta") Long idSeduta, @Param("ordine") Integer ordine);
	
	@Query("SELECT s FROM SottoscrittoreSedutaGiunta s "
			+ "WHERE s.sedutaresoconto.id =:idseduta AND s.profilo.id =:idprofilo")
	SottoscrittoreSedutaGiunta findBySedutaResocontoAndProfilo(@Param("idseduta") Long idSeduta, @Param("idprofilo") Long profilo);
}

package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.SedutaGiunta;

/**
 * Spring Data JPA repository for the SedutaGiunta entity.
 */
public interface SedutaGiuntaRepository extends
		JpaRepository<SedutaGiunta, Long>,
		QueryDslPredicateExecutor<SedutaGiunta>,
		SedutaGiuntaRepositoryCustom {
	
	@Query(value="SELECT MAX(aOdg.numeroArgomento) FROM AttiOdg aOdg WHERE (aOdg.nargOde is null or aOdg.nargOde = false) and aOdg.ordineGiorno.sedutaGiunta.organo = ?3 and ?1 <= aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva and aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva <= ?2")
	public Integer getMaxNumeroArgomentoInsediamento(DateTime inizioInsediamento, DateTime fineInsediamento, String organo);
	
	@Query(value="SELECT MAX(aOdg.numeroArgomento) FROM AttiOdg aOdg WHERE (aOdg.nargOde is null or aOdg.nargOde = false) and aOdg.numeroArgomento = ?5 and (?4 is null or aOdg.ordineGiorno.sedutaGiunta.id <> ?4) and aOdg.ordineGiorno.sedutaGiunta.organo = ?3 and ?1 <= aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva and aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva <= ?2")
	public Integer getNumeroArgomentoInsediamentoEq(DateTime inizioInsediamento, DateTime fineInsediamento, String organo, Long notInSedutaId, Integer numArgomento);
	
	@Query(value="SELECT MAX(aOdg.numeroArgomento) FROM AttiOdg aOdg WHERE (aOdg.nargOde is null or aOdg.nargOde = false) and aOdg.numeroArgomento < ?5 and (?4 is null or aOdg.ordineGiorno.sedutaGiunta.id <> ?4) and aOdg.ordineGiorno.sedutaGiunta.organo = ?3 and ?1 <= aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva and aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva <= ?2")
	public Integer getNumeroArgomentoInsediamentoLt(DateTime inizioInsediamento, DateTime fineInsediamento, String organo, Long notInSedutaId, Integer numArgomento);
	
	@Query(value="SELECT MAX(aOdg.numeroArgomento) FROM AttiOdg aOdg WHERE (aOdg.nargOde is null or aOdg.nargOde = false) and aOdg.numeroArgomento > ?5 and (?4 is null or aOdg.ordineGiorno.sedutaGiunta.id <> ?4) and aOdg.ordineGiorno.sedutaGiunta.organo = ?3 and ?1 <= aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva and aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva <= ?2")
	public Integer getNumeroArgomentoInsediamentoGt(DateTime inizioInsediamento, DateTime fineInsediamento, String organo, Long notInSedutaId, Integer numArgomento);
	
	@Query(value="SELECT MAX(aOdg.numeroArgomento) FROM AttiOdg aOdg WHERE (aOdg.nargOde is null or aOdg.nargOde = false) and ?5 < aOdg.numeroArgomento and aOdg.numeroArgomento < ?5 and (?4 is null or aOdg.ordineGiorno.sedutaGiunta.id <> ?4) and aOdg.ordineGiorno.sedutaGiunta.organo = ?3 and ?1 <= aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva and aOdg.ordineGiorno.sedutaGiunta.inizioLavoriEffettiva <= ?2")
	public Integer getNumeroArgomentoInsediamentoBt(DateTime inizioInsediamento, DateTime fineInsediamento, String organo, Long notInSedutaId, Integer numArgomento);
	
	@Query(value="SELECT aOdg.ordineGiorno.sedutaGiunta FROM AttiOdg aOdg WHERE aOdg.numeroArgomento > 0 "
			+ " AND aOdg.ordineGiorno.sedutaGiunta.fase = ?1 AND aOdg.ordineGiorno.sedutaGiunta.organo = ?2 "
			+ " ORDER BY aOdg.ordineGiorno.sedutaGiunta.id DESC") 
	public List<SedutaGiunta> getLastSeduteByFase(String fase, String organo);
	
	@Query(value="select seduta.primaConvocazioneFine from SedutaGiunta seduta where seduta.id = ?1")
	public DateTime getPrimaConvocazioneFine(Long sedutaId);
	
	@Query(value="select seduta.id from SedutaGiunta seduta where seduta.primaConvocazioneInizio >= ?1 and seduta.primaConvocazioneInizio < ?2 and numero = ?3 and organo = ?4")
	public Long getIdByDateAndNumero(DateTime startDateCheck,DateTime endDateCheck, String numero, String organo);
}

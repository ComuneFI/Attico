package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;

/**
 * Spring Data JPA repository for the Atto entity.
 */
public interface AttoRepository extends JpaRepository<Atto,Long>,QueryDslPredicateExecutor<Atto>,AttoRepositoryCustom  {

	public Atto findByProcessoBpmId(String processoBpmId);
	
	@Query("select distinct atto.stato from Atto atto where atto.tipoAtto.codice = ?1")
	List<String> findDistinctStato(String tipoAtto);
	
	@Query("select distinct atto.stato from Atto atto")
	List<String> findAllDistinctStato();
	
	@Query(value="select count(*) from documentopdf where atto_id = ?1 and firmatodatutti is true", nativeQuery=true)
	public BigInteger countPropostaAttoFirmatadatutti(Long attoId);
	
	@Modifying
	@Query(value="update atto set inizio_pubblicazione_presunta = ?1, fine_pubblicazione_presunta = ?2 where id = ?3", nativeQuery = true)
	public void updateDataPresuntaInizioFinePubblicazione(String inizioPresunta, String finePresunta, Long attoId);
	
	@Modifying
	@Query(value="update atto set inizio_pubblicazione_albo = ?1, fine_pubblicazione_albo = ?2 where id = ?3", nativeQuery = true)
	public void updateDataInizioFinePubblicazione(String inizioPub, String finePub, Long attoId);
	
	@Modifying
	@Query(value="update atto set stato_relata = ?1 where id = ?2", nativeQuery = true)
	public void updateStatoRelata(int statoRelata, Long attoId);
	
	public Atto findByCodiceCifra(String codiceCifra);
	
	@Query(value="select id from atto where codice_cifra = ?1", nativeQuery = true)
	public BigInteger getIdByCodiceCifra(String codiceCifra);
	
	public List<Atto> findByCodicecifraAttoRevocato(String codiceCifra);
	
	@Query(value="select riservato from atto where id = ?1", nativeQuery = true)
	public Byte isAttoRiservato(Long attoId);
	
	@Query(value="select id from atto where tipoatto_id = ?1 and stato in ?2", nativeQuery = true)
	public List<BigInteger> findAttoIdsByTipoAttoAndStatoIn(Long tipoAttoId, List<String> stati);
	
	@Query("select atto.aoo from Atto atto where atto.id = ?1")
	public Aoo getAooOfAtto(Long attoId);
	
	@Modifying
	@Query(value = "update atto set stato = ?1 where id = ?2", nativeQuery = true)
	public void updateStatoAtto(String stato, Long attoId);
	
	@Modifying
	@Query(value = "update atto set stato_pubblicazione = ?1 where id = ?2", nativeQuery = true)
	public void updateStatoPubblicazioneAtto(String statoPubblicazione, Long attoId);
	
	@Modifying
	@Query(value = "update atto set stato_procedura_pubblicazione = ?1 where id = ?2", nativeQuery = true)
	public void updateStatoProceduraPubblicazioneAtto(String statoProceduraPubblicazione, Long attoId);
	
	@Modifying
	@Query(value = "update atto set stato_richiesta_annullamento = ?1 where id = ?2", nativeQuery = true)
	public void updateStatoRichiestaAnnullamentoAtto(String statoProceduraPubblicazione, Long attoId);
	
	@Query(value = "select id from atto where stato = ?1 order by id desc limit 1", nativeQuery=true)
	public Long getAttoTestByStato(String stato);
	
	@Query(value="select new map(atto.oggetto as oggetto, atto.tipoAtto.codice as tipoAtto, atto.codiceCifra as codiceCifra, atto.createdBy as createdBy) from Atto atto where atto.id = ?1")
	List<Map<String, String>> getScrivaniaInfoForAttoId(Long idAtto);
	
	//@Query(value = "select atto from Atto atto where atto.id in (:ids)")
	List<Atto> findByIdIn(@Param("ids") Set<Long> ids);
	
	@Modifying
	@Query(value = "update atto set motivazione_richiesta_annullamento = ?1, oscuramento_atto_pubblicato = false, data_richiesta_annullamento = ?2, richiedente_annullamento = ?3 where id = ?4", nativeQuery = true)
	public void updateDatiAnnullamentoAttoSenzaOscuramento(String motivazioneRichiestaAnnullamento, String data, String richiedente, Long attoId);
	
	@Modifying
	@Query(value = "update atto set motivazione_richiesta_annullamento = ?1, oscuramento_atto_pubblicato = true, data_richiesta_annullamento = ?2, richiedente_annullamento = ?3 where id = ?4", nativeQuery = true)
	public void updateDatiAnnullamentoAttoConOscuramento(String motivazioneRichiestaAnnullamento, String data, String richiedente, Long attoId);
	
	@Query(value="select count(*) from atto where tipoatto_id = ?1", nativeQuery=true)
	public Long countByTipoAttoId(Long tipoAttoId);
	
	@Query(value="select count(*) from atto where aoo_id = ?1", nativeQuery=true)
	public Long countByAooId(Long aooId);
	
	@Query(value="select count(*) from atto where tipoadempimento_id = ?1", nativeQuery=true)
	public Long countByTipoadempimentoId(Long tipoadempimentoId);
	
	@Query(value="select count(*) from atto where qualifica_emanante_id = ?1", nativeQuery=true)
	public Long countByQualificaEmananteId(Long qualificaEmananteId);
	
	@Query(value="select count(*) from atto where tipomateria_id = ?1", nativeQuery=true)
	public Long countByTipomateriaId(Long tipomateriaId);
	
	@Query(value="select count(*) from atto where materia_id = ?1", nativeQuery=true)
	public Long countByMateriaId(Long materiaId);
	
	@Query(value="select count(*) from atto where sottomateria_id = ?1", nativeQuery=true)
	public Long countBySottomateriaId(Long sottomateriaId);
	
	@Query(value="select count(*) from atto where tipodeterminazione_id = ?1", nativeQuery=true)
	public Long countByTipodeterminazioneId(Long tipodeterminazioneId);
	
	@Query(value="select count(*) from atto where cat_obbligo_dl33_id = ?1", nativeQuery=true)
	public Long countByCatObbligoDl33Id(Long catObbligoDl33Id);

	@Query(value="select count(*) from atto where macro_cat_obbligo_dl33_id = ?1", nativeQuery=true)
	public Long countByMacroCatObbligoDl33Id(Long macroCatObbligoDl33Id);
	
	@Query(value="select count(*) from atto where obbligo_dl33_id = ?1", nativeQuery=true)
	public Long countByObbligoDl33Id(Long obbligoDl33Id);
	
	@Query(value="select count(*) from atto where ufficio_id = ?1", nativeQuery=true)
	public Long countByUfficioId(Long ufficioId);
	
	@Query(value="select tipoatto_id from atto where id = ?1", nativeQuery=true)
	public Long getTipoAtto(Long attoId);
	
}

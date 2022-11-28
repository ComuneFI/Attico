package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Scheda;

/**
 * Spring Data JPA repository for the Scheda entity.
 */
public interface SchedaRepository extends JpaRepository<Scheda,Long>, QueryDslPredicateExecutor<Scheda> {
	/**
	 * Rimuove le schededato della scheda con id schedaId
	 * @param schedaId
	 * @return
	 */
	@Modifying
	@Query(value = "delete from schedadato where scheda_id = ?1", nativeQuery = true)
	public Integer deleteSchedaDatoOfSchedaId(Long schedaId);
	
	/**
	 * Ritorna il numero di obblighi che utilizzano la scheda avente schedaId
	 * @param schedaId
	 * @return
	 */
	@Query(value = "select count(*) from obbligo_dl33_scheda where schedas_id = ?1", nativeQuery = true)
	public Integer countObblighiThatUseSchedaId(Long schedaId);
	
	/**
	 * Elimina gli obblighi che utilizzano la scheda avente schedaId
	 * @param schedaId
	 * @return
	 */
	@Modifying
	@Query(value = "delete from obbligo_dl33_scheda where schedas_id = ?1", nativeQuery = true)
	public void deleteObblighiThatUseSchedaId(Long schedaId);
	
	/**
	 * Ritorna la lista di Id degli obblighi che utilizzano la scheda avente schedaId
	 * @param schedaId
	 * @return
	 */
	@Query(value = "select distinct obbligo_dl33s_id from obbligo_dl33_scheda where schedas_id = ?1", nativeQuery = true)
	public List<BigInteger> selectObblighiIdsThatUseSchedaId(Long schedaId);
	
	/**
	 * Ritorna il numero di atti che utilizzano la scheda avente schedaId
	 * @param schedaId
	 * @return
	 */
	@Query(value = "select count(*) from atto_has_datidlg33 where scheda_id = ?1 group by atto_id", nativeQuery = true)
	public Integer countAttiThatUseSchedaId(Long schedaId);
}

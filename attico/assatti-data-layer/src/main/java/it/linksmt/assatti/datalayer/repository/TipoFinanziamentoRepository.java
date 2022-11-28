package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoFinanziamento;

/**
 * Spring Data JPA repository for the TipoFinanziamento entity.
 */
public interface TipoFinanziamentoRepository extends JpaRepository<TipoFinanziamento,Long>,QueryDslPredicateExecutor<TipoFinanziamento> {
	List<TipoFinanziamento> findByCodice(String codice);
	
	@Query("SELECT t from TipoFinanziamento t where t.codice like %?1% ")
	List<TipoFinanziamento> findByLikeCodice(String codice);
	
	@Query(value="select descrizione from tipofinanziamento where codice = ?1", nativeQuery=true)
	String getDescrizioneByCodiceTipoFinanziamento(String codiceTipoFinanziamento);
	
	@Modifying
	@Query(value="update tipofinanziamento set enabled = ?1 where id = ?2", nativeQuery=true)
	public void enableDisable(boolean enabled, Long idTipoFinanziamento);
}

package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Ruolo;

/**
 * Spring Data JPA repository for the Ruolo entity.
 */
public interface RuoloRepository extends JpaRepository<Ruolo,Long>,QueryDslPredicateExecutor<Ruolo> {
	List<Ruolo> findByCodice(String codice);
	
	@Query("SELECT r from Ruolo r where r.codice like %?1% ")
	List<Ruolo> findByLikeCodice(String codice);
	
	@Query(value="select descrizione from ruolo where codice = ?1", nativeQuery=true)
	String getDescrizioneByCodiceRuolo(String codiceRuolo);
	
	@Modifying
	@Query(value="update ruolo set enabled = ?1 where id = ?2", nativeQuery=true)
	public void enableDisable(boolean enabled, Long idRuolo);
	
	@Query(value="select count(*) from ruolo_hasgruppi where ruolo_id = ?1", nativeQuery=true)
	public Integer countGruppiRuoloOfRuolo(Long ruoloId);
}

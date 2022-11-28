package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Profilo;

/**
 * Spring Data JPA repository for the Profilo entity.
 */
public interface ProfiloRepository extends JpaRepository<Profilo, Long>, QueryDslPredicateExecutor<Profilo>, ProfiloRepositoryCustom  {

	List<Profilo> findByAooId(Long aooId);
	
//	@Query("select p from Profilo p where p.utente.id = ?1 and p.aoo.id = ?2 and p.tipiAtto.tipoAtto.id = ?3 and p.validita.validoal is null")
	@Query(value = "select * from profilo p inner join profilotipoatto pta on p.id = pta.profilo_id where p.utente_id = ?1 and p.aoo_id = ?2 and pta.tipoatto_id = ?3 and p.validoal is null", nativeQuery = true)
	List<Profilo> findActiveByUtenteAooTipoatto(Long userId, Long aooId, Long tipoAttoId);
	
	@Query(value = "select p from Profilo p where p.utente.username = ?1 and p.validita.validoal is null")
	List<Profilo> findActiveByUsername(String username);
	
	@Query(value = "select * from profilo p inner join profilotipoatto pta on p.id = pta.profilo_id where p.utente_id = ?1 and p.validoal is null", nativeQuery = true)
	List<Profilo> findActiveByUtenteId(Long userId);
	
	@Query(value = "select p.id from profilo p where p.utente_id = ?1 and p.validoal is null", nativeQuery = true)
	List<BigInteger> findActiveIdsByUtenteId(Long userId);
	
	@Query(value = "SELECT codice FROM ruolo R inner join ruolo_hasgruppi HG on R.id = HG.ruolo_id inner join grupporuolo GR on GR.id = HG.grupporuolo_id inner join profilo P on P.grupporuolo_id = GR.id where P.id = ?1", nativeQuery=true)
	List<String> findRuoliOfProfile(Long profiloId);
	
	@Query("select p from Profilo p where p.grupporuolo.denominazione = ?1 and p.validita.validoal is null")
	List<Profilo> findActiveByGruppoRuolo(String nomeGruppoRuolo);
	
	@Query("select p from Profilo p where p.grupporuolo.id = ?1 and p.validita.validoal is null")
	List<Profilo> findActiveByGruppoRuoloId(Long gruppoRuoloId);

}

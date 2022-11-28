package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Utente;

/**
 * Spring Data JPA repository for the Utente entity.
 */
public interface UtenteRepository extends JpaRepository<Utente,Long>,QueryDslPredicateExecutor<Utente> {
	public Utente findByCodicefiscale(String codiceFiscale);
	public Utente findByUsername(String username);
	
	@Query(value = "SELECT CONCAT(aoo.codice, '-', aoo.descrizione) From utente inner join profilo on profilo.utente_id = utente.id inner join aoo on profilo.aoo_id = aoo.id WHERE utente.id=?1 and profilo.validoal is null group by aoo.id", nativeQuery = true)
	public List<String> getAooDescriptionsByUser(Long utenteId);
	
	@Query("select utente from Utente utente WHERE CONCAT(utente.nome, ' ', utente.cognome) LIKE ?1")
	List<Utente> findByNomeCognome(String CognomeNome);
	
	@Query(value = "select * from utente where id in (select distinct utente_id from profilo where id in(select id from aoo where profiloresponsabile_id is not null and (validoal is null or validoal > now())) and (validoal is null or validoal > now())) and stato = 1 order by username", nativeQuery = true)
	public List<Utente> findDirigentiAttivi();
	
	public List<Utente> findByHasProfiliAooIdOrderByUsernameAsc(Long idAoo);
	
	public List<Utente> findByHasProfiliAooIdAndUsernameOrderByUsernameAsc(Long idAoo, String username);
	
//	@Query(value = "SELECT CONCAT(utente.nome, ' ', utente.cognome) From utente WHERE utente.username=?1", nativeQuery = true)
//	public String getNameByUser(String username);
	
}

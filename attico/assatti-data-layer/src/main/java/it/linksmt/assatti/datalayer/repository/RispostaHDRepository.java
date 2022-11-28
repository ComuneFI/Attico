package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.RispostaHD;

/**
 * Spring Data JPA repository for the RispostaHD entity.
 */
public interface RispostaHDRepository extends JpaRepository<RispostaHD,Long>, QueryDslPredicateExecutor<RispostaHD>  {
	@Modifying
	@Query(value="update rispostahd set testo_risposta = ?1 where id = ?2", nativeQuery=true)
	public void updateTestoRisposta(String testoRisposta, Long rispostaId);
}

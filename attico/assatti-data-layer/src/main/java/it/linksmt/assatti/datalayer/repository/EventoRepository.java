package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Evento;
import it.linksmt.assatti.datalayer.domain.TipoEvento;

/**
 * Spring Data JPA repository for the Evento entity.
 */
public interface EventoRepository extends JpaRepository<Evento,Long>, QueryDslPredicateExecutor<Evento> {
	List<Evento> findByTipoEventoAndAtto(TipoEvento tipoEvento, Atto atto);
	List<Evento> findByTipoEvento(TipoEvento tipoEvento);
	List<Evento> findByAttoOrderByDataCreazioneAsc(Atto atto);
	
	@Modifying
	@Query(value="insert into evento(atto_id, tipoevento_id, data_creazione) values(?1, (select id from tipoevento where codice = 'ANNULLAMENTO_AUTOMATICO'), NOW())", nativeQuery = true)
	void annullamentoAutomatico(Long attoId);
	
	@Query(value="select distinct atto_id from evento where tipoevento_id = (select id from tipoevento where codice = ?1) and atto_id not in (select atto_id from evento where tipoevento_id = (select id from tipoevento where codice = ?2))", nativeQuery=true)
	List<BigInteger> findAttoIdsWithEventTypeAndNotOtherEvent(String eventoDaIncludere, String eventoDaEscludere);
	
	@Modifying
	@Query(value="insert into esecutivita (atto_id, data_esecutivita) values (?1, NOW())", nativeQuery=true)
	void fillEsecutivita(Long attoId);
	
	@Query(value="select atto_id from esecutivita", nativeQuery=true)
	List<BigInteger> getAttiEsecutivi();
}

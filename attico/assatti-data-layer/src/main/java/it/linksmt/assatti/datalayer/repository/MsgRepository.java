package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Msg;

/**
 * Spring Data JPA repository for the Msg entity.
 */
public interface MsgRepository extends JpaRepository<Msg,Long>, QueryDslPredicateExecutor<Msg>  {
	@Modifying
	@Query(value="update msg set validoal = (CURDATE() - INTERVAL 1 DAY) where id = ?1", nativeQuery=true)
	public void forceExpire(Long idMsg);
	
	@Query(value="select count(*) from msg_utente where utente_id = ?1 and msg_id = ?2", nativeQuery=true)
	public Integer checkMessaggioLetto(Long utenteId, Long idMsg);
	
	@Query(value="select msg_id from msg_utente where utente_id = ?1", nativeQuery=true)
	public List<Long> getMsgIdsLetti(Long utenteId);
	
	@Modifying
	@Query(value="insert msg_utente (utente_id, msg_id) values(?1, ?2)", nativeQuery=true)
	public void setMessaggioLetto(Long utenteId, Long idMsg);
	
}

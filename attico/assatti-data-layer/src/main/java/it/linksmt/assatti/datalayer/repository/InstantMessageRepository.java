package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.InstantMessage;

/**
 * Spring Data JPA repository for the InstantMessage entity.
 */
public interface InstantMessageRepository extends JpaRepository<InstantMessage,Long>,QueryDslPredicateExecutor<InstantMessage> {
	@Modifying
	@Query(value="delete from instant_message where id = ?1", nativeQuery=true)
	public void deleteInstantMessage(Long instantMessageId);
	
	@Query(value="select count(*) from instant_message where username = ?1", nativeQuery=true)
	public Integer countMessagesForUser(String username);
	
	@Query(value="select * from instant_message where username = ?1", nativeQuery=true)
	public List<InstantMessage> getMessagesForUser(String username);
	
	@Modifying
	@Query(value="delete from instant_message where time_invio > (NOW() + INTERVAL 2 minute)", nativeQuery=true)
	public void cleanInstantMessage();
}

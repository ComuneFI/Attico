package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.AttoWorker;

/**
 * Spring Data JPA repository for the AttoWorker entity.
 */
public interface AttoWorkerRepository extends JpaRepository<AttoWorker,Long>, QueryDslPredicateExecutor<AttoWorker>  {
	@Modifying
	@Query(value="delete from attoworker where atto_id = ?1", nativeQuery=true)
	public void iterTerminato(Long attoId);
	
	public AttoWorker findByAttoIdAndProfiloId(Long attoId, Long profiloId);
}

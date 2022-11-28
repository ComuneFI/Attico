package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ReportWorkflowAtto;
import it.linksmt.assatti.datalayer.domain.StatoReportWorkflowAtto;

/**
 * Spring Data JPA repository for the ReportWorkflowAtto entity.
 */
public interface ReportWorkflowAttoRepository extends JpaRepository<ReportWorkflowAtto,Long>, QueryDslPredicateExecutor<ReportWorkflowAtto> {
	List<ReportWorkflowAtto> findByStatoIn(List<StatoReportWorkflowAtto> stati);
	
	@Modifying
	@Query(value="INSERT INTO report_workflow_atto(atto_id, stato, errore, created_date) VALUES(?1, ?2, ?3, now())", nativeQuery=true)
	void insertNewReportWorkflowAtto(Long attoId, String stato, String errore);
	
	@Modifying
	@Query(value="update report_workflow_atto set stato = ?2, errore= ?3 where id = ?1", nativeQuery=true)
	void updateStato(Long id, String stato, String errore);
}

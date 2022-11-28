package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;

/**
 * Spring Data JPA repository for the SottoscrittoreAtto entity.
 */
public interface SottoscrittoreAttoRepository extends JpaRepository<SottoscrittoreAtto,Long>, QueryDslPredicateExecutor<SottoscrittoreAtto> {

	@Query("Select sottoscrittoreatto From SottoscrittoreAtto sottoscrittoreatto where sottoscrittoreatto.aoo.id =:aooid and sottoscrittoreatto.atto.id =:attoid ")
    List<SottoscrittoreAtto> findByAooAtto(@Param("aooid") Long aooid,@Param("attoid") Long attoid);
	 
	List<SottoscrittoreAtto> deleteByAttoId(Long attoId);

	List<SottoscrittoreAtto> deleteByDocumentoPdfId(Long documentoId);
}

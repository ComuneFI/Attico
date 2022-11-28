package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;

/**
 * Spring Data JPA repository for the QualificaProfessionale entity.
 */
public interface QualificaProfessionaleRepository extends JpaRepository<QualificaProfessionale,Long>, QueryDslPredicateExecutor<QualificaProfessionale> {
	
	@Query(value = "select * from qualificaprofessionale where enabled = 1 order by denominazione", nativeQuery = true)
	public List<QualificaProfessionale> findEnabledOrderedByDenominazione();

	@Query(value = "select * from qualificaprofessionale inner join profilo_hasqualifica on qualificaprofessionale.id=profilo_hasqualifica.qualifica_id where profilo_hasqualifica.profilo_id = ?1", nativeQuery = true)
	public List<QualificaProfessionale> findByProfiloId(Long profiloId);
	
	@Query(value = "select * from qualificaprofessionale inner join profilo_hasqualifica on qualificaprofessionale.id=profilo_hasqualifica.qualifica_id where profilo_hasqualifica.profilo_id = ?1 and qualificaprofessionale.enabled=true", nativeQuery = true)
	public List<QualificaProfessionale> findEnabledByProfiloId(Long profiloId);
}

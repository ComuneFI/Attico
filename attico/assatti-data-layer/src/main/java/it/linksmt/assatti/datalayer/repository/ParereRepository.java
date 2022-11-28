package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.linksmt.assatti.datalayer.domain.Parere;

/**
 * Spring Data JPA repository for the Parere entity.
 */
public interface ParereRepository extends JpaRepository<Parere,Long> {
	Parere findByAttoIdAndProfiloId(Long attoId, Long proficoId);
	
	@Query(value="select aoo_id from parere where id = ?1", nativeQuery=true)
	Long getAooIdByParereId(Long parereId);
	
	@Query(value = "select count(*) from parere p where pareres_sintetico = ?1 ", nativeQuery = true)
	Long countByParereSintetico(String parereSintetico);
}

package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Delega;

/**
 * Spring Data JPA repository for the Profilo entity.
 */
public interface DelegaRepository extends JpaRepository<Delega, Long>, QueryDslPredicateExecutor<Delega>{

	@Query(value = "select * from delega d where d.data_validita_inizio<=DATE(NOW()) and d.data_validita_fine>=DATE(NOW()) "
			+ "and d.enabled=1 ORDER BY ?#{#pageable}", nativeQuery = true)
	List<Delega> findByActive(Pageable pageable);
	
	@Query(value = "select d.* from delega d LEFT JOIN delega_profilo_delegato dpd ON d.id = dpd.id_delega "
			+ "where d.data_validita_inizio<=DATE(NOW()) and d.data_validita_fine>=DATE(NOW()) "
			+ "and d.enabled=1 and dpd.id_profilo_delegato = ?1 group by d.profilo_delegante_id", nativeQuery = true)
	List<Delega> findByDelegatoActive(Long idDelegato);
	
	@Query(value = "select d.* from delega d LEFT JOIN delega_profilo_delegato dpd ON d.id = dpd.id_delega "
			+ "where d.data_validita_inizio<=DATE(NOW()) and d.data_validita_fine>=DATE(NOW()) and d.enabled=1 "
			+ "and d.profilo_delegante_id = ?1 and dpd.id_profilo_delegato = ?2", nativeQuery = true)
	List<Delega> findByDeleganteAndDelegatoActive(Long idDelegante, Long idDelegato);
	
	
	@Query(value = "select count(*) from delega d where d.data_validita_inizio<=DATE(NOW()) and d.data_validita_fine>=DATE(NOW()) "
			+ " and d.enabled=1 ", nativeQuery = true)
	Long countByActive();

	@Query(
		value = "select count(*) from delega d LEFT JOIN delega_profilo_delegato dpd ON d.id = dpd.id_delega where d.data_validita_inizio<=DATE(NOW()) "
				+ "and d.enabled=1 and d.data_validita_fine>=DATE(NOW()) and dpd.id_profilo_delegato = ?1 ", nativeQuery = true)
	Long countByDelegatoActive(Long idDelegato);
}

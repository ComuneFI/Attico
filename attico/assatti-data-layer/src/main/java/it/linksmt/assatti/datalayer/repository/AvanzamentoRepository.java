package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Avanzamento;

/**
 * Spring Data JPA repository for the Avanzamento entity.
 */
public interface AvanzamentoRepository extends JpaRepository<Avanzamento,Long>,QueryDslPredicateExecutor<Avanzamento> {

	@Query(value="select attivita from avanzamento where id = ?1", nativeQuery=true)
    String findAttivitaByAvId(Long id);
	
	@Query(value="select distinct attivita from avanzamento", nativeQuery=true)
    List<String> findNomiAttivita();
}

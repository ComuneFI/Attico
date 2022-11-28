package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.MonitoraggioAccesso;

/**
 * Spring Data JPA repository for the MonitoraggioAccesso entity.
 */
public interface MonitoraggioAccessoRepository extends JpaRepository<MonitoraggioAccesso,Long>, QueryDslPredicateExecutor<MonitoraggioAccesso>  {
}

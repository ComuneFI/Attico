package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.SottoMateria;

/**
 * Spring Data JPA repository for the SottoMateria entity.
 */
public interface SottoMateriaRepository extends JpaRepository<SottoMateria,Long> ,QueryDslPredicateExecutor<SottoMateria>{

}

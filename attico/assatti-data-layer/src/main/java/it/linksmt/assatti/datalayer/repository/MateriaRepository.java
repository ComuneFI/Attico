package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Materia;

/**
 * Spring Data JPA repository for the Materia entity.
 */
public interface MateriaRepository extends JpaRepository<Materia,Long> ,QueryDslPredicateExecutor<Materia> {

}

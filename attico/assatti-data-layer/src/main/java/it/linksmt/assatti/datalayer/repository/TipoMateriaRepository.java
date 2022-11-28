package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoMateria;

/**
 * Spring Data JPA repository for the TipoMateria entity.
 */
public interface TipoMateriaRepository extends JpaRepository<TipoMateria,Long> ,QueryDslPredicateExecutor<TipoMateria> {

	List<TipoMateria> findByAooIsNull();

	List<TipoMateria> findByAooId(Long aooId);
}

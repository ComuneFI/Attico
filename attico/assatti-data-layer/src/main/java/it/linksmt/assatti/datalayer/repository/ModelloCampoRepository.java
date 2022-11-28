package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ModelloCampo;
import com.mysema.query.types.Predicate;

/**
 * Spring Data JPA repository for the ModelloCampo entity.
 */
public interface ModelloCampoRepository extends JpaRepository<ModelloCampo,Long>, QueryDslPredicateExecutor<ModelloCampo>  {
	
	@Override
	public Page<ModelloCampo> findAll(Predicate predicate, Pageable pageable);
	

}

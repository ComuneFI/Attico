package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.CategoriaMsg;

/**
 * Spring Data JPA repository for the CategoriaMsg entity.
 */
public interface CategoriaMsgRepository extends JpaRepository<CategoriaMsg,Long>, QueryDslPredicateExecutor<CategoriaMsg>  {
	@Modifying
	@Query(value="update categoriamsg set enabled = ?1 where id = ?2", nativeQuery=true)
	public void enableDisable(boolean enabled, Long idCategoriaMsg);
}

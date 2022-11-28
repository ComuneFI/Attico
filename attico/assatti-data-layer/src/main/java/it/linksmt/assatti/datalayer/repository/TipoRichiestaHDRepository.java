package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoRichiestaHD;

/**
 * Spring Data JPA repository for the TipoRichiestaHD entity.
 */
public interface TipoRichiestaHDRepository extends JpaRepository<TipoRichiestaHD,Long>, QueryDslPredicateExecutor<TipoRichiestaHD> {
	@Modifying
	@Query(value="update tiporichiestahd set enabled = ?1 where id = ?2", nativeQuery=true)
	public void enableDisable(boolean enabled, Long idTipoRichiesta);
	
}

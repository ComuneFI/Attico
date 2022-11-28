package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.Obbligo_DL33;

/**
 * Spring Data JPA repository for the Obbligo_DL33 entity.
 */
public interface Obbligo_DL33Repository extends JpaRepository<Obbligo_DL33,Long>, QueryDslPredicateExecutor<Obbligo_DL33> {

    @Query("select obbligo_DL33 from Obbligo_DL33 obbligo_DL33 left join fetch obbligo_DL33.schedas where obbligo_DL33.id =:id")
    Obbligo_DL33 findOneWithEagerRelationships(@Param("id") Long id);

    @Modifying
	@Query(value="update obbligo_dl33 set attivo = true where id = ?1", nativeQuery=true)
	void abilitaObbligo(Long id);
	
	@Modifying
	@Query(value="update obbligo_dl33 set attivo = false where id = ?1", nativeQuery=true)
	void disabilitaObbligo(Long id);
	
	@Query(value = "select * from obbligo_dl33 where cat_obbligo_dl33_id = ?1 and codice = ?2", nativeQuery = true)
	public List<Obbligo_DL33> findByCategoriaIdECodice(Long cat_obbligo_dl33_id, String codice);
}

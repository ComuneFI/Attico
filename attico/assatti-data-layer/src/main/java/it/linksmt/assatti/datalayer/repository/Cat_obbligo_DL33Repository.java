package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Cat_obbligo_dl33;

/**
 * Spring Data JPA repository for the Cat_obbligo_DL33 entity.
 */
public interface Cat_obbligo_DL33Repository extends JpaRepository<Cat_obbligo_dl33,Long>, QueryDslPredicateExecutor<Cat_obbligo_dl33> {
	@Query(value="select new map(obbligo.id as id, obbligo.codice as codice, obbligo.descrizione as descrizione) from Obbligo_DL33 obbligo where obbligo.cat_obbligo_DL33.id = ?1 order by obbligo.id asc")
	List<String> findObblighiOfCategoria(Long idCategoria);
	
	@Modifying
	@Query(value="update cat_obbligo_dl33 set attiva = true where id = ?1", nativeQuery=true)
	void abilitaCategoria(Long id);
	
	@Modifying
	@Query(value="update cat_obbligo_dl33 set attiva = false where id = ?1", nativeQuery=true)
	void disabilitaCategoria(Long id);
	
	@Query(value = "select * from cat_obbligo_dl33 where fk_cat_obbligo_macro_cat_obbligo_idx_id = ?1 and codice = ?2", nativeQuery = true)
	public List<Cat_obbligo_dl33> findByMacroIdECodice(Long fk_cat_obbligo_macro_cat_obbligo_idx_id, String codice);
}

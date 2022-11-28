package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Macro_cat_obbligo_dl33;

/**
 * Spring Data JPA repository for the Macro_cat_obbligo_dl33 entity.
 */
public interface Macro_cat_obbligo_dl33Repository extends JpaRepository<Macro_cat_obbligo_dl33,Long>, QueryDslPredicateExecutor<Macro_cat_obbligo_dl33> {
	List<Macro_cat_obbligo_dl33> findByAttiva(Boolean attiva);
	
	@Query(value="select new map(categoria.id as id, categoria.codice as codice, categoria.descrizione as descrizione) from Cat_obbligo_dl33 categoria where categoria.fk_cat_obbligo_macro_cat_obbligo_idx.id = ?1 order by categoria.fk_cat_obbligo_macro_cat_obbligo_idx.id asc")
	List<String> findCategorieOfMacroCategoria(Long idMacroCategoria);
	
	@Query(value="select new map(obbligo.id as id, obbligo.codice as codice, obbligo.descrizione as descrizione) from Obbligo_DL33 obbligo where obbligo.cat_obbligo_DL33.id in (select categoria.id from Cat_obbligo_dl33 categoria where categoria.fk_cat_obbligo_macro_cat_obbligo_idx.id = ?1) order by obbligo.cat_obbligo_DL33.id asc")
	List<String> findObblighiOfMacroCategoria(Long idMacroCategoria);
	
	@Query(value = "select * from macro_cat_obbligo_dl33 where codice = ?1", nativeQuery = true)
	public List<Macro_cat_obbligo_dl33> findByCodice(String codice);
	
	@Modifying
	@Query(value="update macro_cat_obbligo_dl33 set attiva = true where id = ?1", nativeQuery=true)
	void abilitaMacroCategoria(Long id);
	
	@Modifying
	@Query(value="update macro_cat_obbligo_dl33 set attiva = false where id = ?1", nativeQuery=true)
	void disabilitaMacroCategoria(Long id);
}

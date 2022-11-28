package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.GruppoRuolo;

/**
 * Spring Data JPA repository for the GruppoRuolo entity.
 */
public interface GruppoRuoloRepository extends JpaRepository<GruppoRuolo,Long>, QueryDslPredicateExecutor<GruppoRuolo> {

    @Query("select gruppoRuolo from GruppoRuolo gruppoRuolo where gruppoRuolo.id =:id")
    GruppoRuolo findOneWithEagerRelationships(@Param("id") Long id);
    
    @Query("SELECT gr from GruppoRuolo gr where gr.denominazione like %?1% ")
    List<GruppoRuolo> findByLikeDenominazione(String denominazione);
}

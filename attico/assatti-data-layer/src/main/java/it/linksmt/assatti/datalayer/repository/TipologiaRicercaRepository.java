package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.TipologiaRicerca;

/**
 * Spring Data JPA repository for the TipologiaRicerca entity.
 */
public interface TipologiaRicercaRepository extends JpaRepository<TipologiaRicerca,Long>, QueryDslPredicateExecutor<TipologiaRicerca> {

    @Query("select tipologiaRicerca from TipologiaRicerca tipologiaRicerca where tipologiaRicerca.id =:id")
    TipologiaRicerca findOneWithEagerRelationships(@Param("id") Long id);
    
    @Query("SELECT tr from TipologiaRicerca tr where tr.code like %?1% ")
    List<TipologiaRicerca> findByLikeCode(String code);
}

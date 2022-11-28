package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.TipoAtto;

/**
 * Spring Data JPA repository for the TipoAtto entity.
 */
public interface TipoAttoRepository extends JpaRepository<TipoAtto,Long>, QueryDslPredicateExecutor<TipoAtto> {
	@Query(value="select distinct stato from atto where tipoatto_id = ?1 and stato is not null", nativeQuery = true)
	public List<String> getStatiByTipoAttoId(Long tipoAttoId);
	
	public TipoAtto findByCodice(String codice);
	List<TipoAtto> findByIdIn(List<Long> ids);
}

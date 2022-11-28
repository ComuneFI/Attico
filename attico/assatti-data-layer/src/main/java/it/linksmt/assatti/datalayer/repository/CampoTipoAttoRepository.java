package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Campo;
import it.linksmt.assatti.datalayer.domain.CampoTipoAtto;
import it.linksmt.assatti.datalayer.domain.CampoTipoAttoId;

/**
 * Spring Data JPA repository for the CampoTipoAtto entity.
 */
public interface CampoTipoAttoRepository extends JpaRepository<CampoTipoAtto, CampoTipoAttoId>, QueryDslPredicateExecutor<Campo> {
	@Query("select c from CampoTipoAtto c where c.tipoAtto.id = ?1")
	List<CampoTipoAtto> findByTipoAtto(Long idTipoAtto);

}

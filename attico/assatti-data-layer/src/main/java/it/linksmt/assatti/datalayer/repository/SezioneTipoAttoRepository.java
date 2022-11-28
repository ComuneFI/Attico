package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Sezione;
import it.linksmt.assatti.datalayer.domain.SezioneTipoAtto;
import it.linksmt.assatti.datalayer.domain.SezioneTipoAttoId;

/**
 * Spring Data JPA repository for the SezioneTipoAtto entity.
 */
public interface SezioneTipoAttoRepository extends JpaRepository<SezioneTipoAtto, SezioneTipoAttoId>, QueryDslPredicateExecutor<SezioneTipoAtto> {
	@Query("select s from SezioneTipoAtto s where s.tipoAtto.id = ?1")
	List<SezioneTipoAtto> findByTipoAtto(Long idTipoAtto);

}

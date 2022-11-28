package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ProgressivoProposta;

/**
 * Spring Data JPA repository for the ProgressivoProposta entity.
 */
public interface ProgressivoPropostaRepository extends JpaRepository<ProgressivoProposta, Long>, QueryDslPredicateExecutor<ProgressivoProposta> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoProposta getByAnnoAndAooIdAndTipoProgressivoId(Integer anno, Long aooId, Long tipoProgressivoId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoProposta getByAnnoAndTipoProgressivoId(Integer anno, Long tipoProgressivoId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoProposta getByAnnoAndTipoProgressivoIdAndAooIdIsNull(Integer anno, Long tipoProgressivoId);

	public List<ProgressivoProposta> findByAooIdAndAnno(Long aooID, Integer anno);

	public List<ProgressivoProposta> findByAnnoAndAooIdAndTipoProgressivoId(Integer anno, Long aooId, Long tipoProgressivoId);

	public List<ProgressivoProposta> findByAnnoAndTipoProgressivoIdAndAooIdIsNull(Integer anno, Long tipoProgressivoId);

}

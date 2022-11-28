package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.ProgressivoAdozione;

/**
 * Spring Data JPA repository for the ProgressivoAdozione entity.
 */
public interface ProgressivoAdozioneRepository extends JpaRepository<ProgressivoAdozione, Long>, QueryDslPredicateExecutor<ProgressivoAdozione> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoAdozione getByAnnoAndAooIdAndTipoProgressivoId(Integer anno, Long aooId, Long tipoProgressivoId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoAdozione getByAnnoAndTipoProgressivoId(Integer anno, Long tipoProgressivoId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoAdozione getByAnnoAndTipoProgressivoIdAndAooIsNull(Integer anno, Long tipoProgressivoId);

	public List<ProgressivoAdozione> findByAooIdAndAnno(Long aooID, Integer anno);

	public List<ProgressivoAdozione> findByAnnoAndAooIdAndTipoProgressivoId(Integer anno, Long aooId, Long tipoProgressivoId);

	public List<ProgressivoAdozione> findByAnnoAndTipoProgressivoIdAndAooIsNull(Integer anno, Long tipoProgressivoId);

}

package it.linksmt.assatti.datalayer.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import it.linksmt.assatti.datalayer.domain.ProgressivoOrdineGiorno;

/**
 * Spring Data JPA repository for the ProgressivoProposta entity.
 */
public interface ProgressivoOrdineGiornoRepository extends JpaRepository<ProgressivoOrdineGiorno, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoOrdineGiorno getByAnnoAndTipoOdgId(Integer anno, Long tipoOdgId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoOrdineGiorno getByAnno(Integer anno);

}

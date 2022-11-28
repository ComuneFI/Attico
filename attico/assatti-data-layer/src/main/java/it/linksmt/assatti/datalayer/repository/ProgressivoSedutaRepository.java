package it.linksmt.assatti.datalayer.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import it.linksmt.assatti.datalayer.domain.ProgressivoSeduta;

public interface ProgressivoSedutaRepository extends JpaRepository<ProgressivoSeduta, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public ProgressivoSeduta getByAnnoAndOrgano(Integer anno, String organo);

}

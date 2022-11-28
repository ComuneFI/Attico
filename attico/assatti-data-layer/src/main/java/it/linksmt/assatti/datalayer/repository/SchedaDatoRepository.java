package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.linksmt.assatti.datalayer.domain.SchedaDato;

/**
 * Spring Data JPA repository for the SchedaDato entity.
 */
public interface SchedaDatoRepository extends JpaRepository<SchedaDato,Long> {
	
	List<SchedaDato> findByDatoId(Long datoId);

}

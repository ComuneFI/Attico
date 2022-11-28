package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.linksmt.assatti.datalayer.domain.AttoSchedaDato;
import it.linksmt.assatti.datalayer.domain.AttoSchedaDatoId;

/**
 * Spring Data JPA repository for the Atto entity.
 */
public interface AttoSchedaDatoRepository extends JpaRepository<AttoSchedaDato,AttoSchedaDatoId>   {

}

package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.Fattura;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Fattura entity.
 */
public interface FatturaRepository extends JpaRepository<Fattura,Long> {

}

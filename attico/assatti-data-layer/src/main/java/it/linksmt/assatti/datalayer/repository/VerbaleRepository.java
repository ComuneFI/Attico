package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.Verbale;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Verbale entity.
 */
public interface VerbaleRepository extends JpaRepository<Verbale,Long> {

}

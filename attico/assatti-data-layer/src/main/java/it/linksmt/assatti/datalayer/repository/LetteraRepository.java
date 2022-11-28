package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.Lettera;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Lettera entity.
 */
public interface LetteraRepository extends JpaRepository<Lettera,Long> {

}

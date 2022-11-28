package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.ReportRuntime;
import org.springframework.data.jpa.repository.*;



/**
 * Spring Data JPA repository for the ReportRuntime entity.
 */
public interface ReportRuntimeRepository extends JpaRepository<ReportRuntime,Long> {

}

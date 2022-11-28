package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.Beneficiario;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Beneficiario entity.
 */
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

}

package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;

/**
 * Spring Data JPA repository for the DocumentoInformatico entity.
 */
public interface DocumentoInformaticoRepository extends JpaRepository<DocumentoInformatico,Long> {
	
	List<DocumentoInformatico> findAllByAttoId(Long id);
	
	@Query(value="select count(*) from documentoinformatico where id = ?1 and allegato_provvedimento is true", nativeQuery=true)
	BigInteger isAllegatoProvvedimento(BigInteger documentoInformaticoId);
	
	Page<DocumentoInformatico> findByParteIntegranteIsFalseOrParteIntegranteIsNull(Pageable pageable);

	@Query(value = "select distinct documentopdf_id, documentoinformatico_id "
			+ " from documentoserie"
			+ " WHERE documentopdf_id is not null and documentoinformatico_id is not null and da_pubblicare is true ", nativeQuery = true)
	public List<Object[]> findByAttoDaRiversareInAlbo();

}

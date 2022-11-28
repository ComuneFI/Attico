package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Composizione;

/**
 * Spring Data JPA repository for the Composizione entity.
 */
public interface ComposizioneRepository extends JpaRepository<Composizione,Long>, QueryDslPredicateExecutor<Composizione> {

    
    @Query("SELECT c from Composizione c where c.predefinita = true and c.organo like %?1% ")
    Composizione findOneByPredefinitaAndOrgano(String organo);
    
    @Modifying
	@Query(value="update composizione set predefinita = false where id != ?1 and organo like %?2%", nativeQuery = true)
	public void annullaPredefinita(Long idNuovaPredefinita, String organo);
    
    @Query("select c from Composizione c where c.organo like %?1% order by c.predefinita desc, c.dataCreazione desc")
	List<Composizione> findComposizioniByOrgano(String organo);
    
}

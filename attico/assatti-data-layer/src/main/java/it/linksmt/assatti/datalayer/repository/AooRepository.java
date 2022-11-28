package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.Aoo;

/**
 * Spring Data JPA repository for the Aoo entity.
 */
public interface AooRepository extends JpaRepository<Aoo,Long>, QueryDslPredicateExecutor<Aoo> {
	
	@Query("select aoo from Aoo aoo left join fetch aoo.hasAssessorati where aoo.id =:id")
    Aoo findOneWithEagerRelationships(@Param("id") Long id);
	
	@Query("select aoo from Profilo profilo where profilo.id = ?1")
	public Aoo findAooByProfiloId(Long profiloId);
	
	@Query(value="select aoo from Aoo aoo where codice = ?1 and (validita.validoal is null or validita.validoal > now())")
	public List<Aoo> findAoosByCodice(String codice);
	
}

package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.RiversamentoInAttesa;

/**
 * Spring Data JPA repository for the RiversamentoInAttesa entity.
 */
public interface RiversamentoPoolRepository extends JpaRepository<RiversamentoInAttesa,Long>,QueryDslPredicateExecutor<RiversamentoInAttesa>  {
	@Modifying
	@Query(value="insert into ultimo_riversamento_doc_principale (documentopdf_id, configurazione_riversamento_id, is_rup) values (?1, ?2, ?3)", nativeQuery=true)
	void saveLastDocPrincipaleRiversato(Long documentopdfId, Long configurazioneRiversamentoId, boolean isRup);
	
	@Modifying
	@Query(value="delete from ultimo_riversamento_doc_principale", nativeQuery=true)
	void clearLastDocPrincipaleRiversato();

	@Query(value="select documentopdf_id, configurazione_riversamento_id, is_rup from ultimo_riversamento_doc_principale limit 1", nativeQuery=true)
	List<Object[]> findLastDocPrincipaleRiversato();
}

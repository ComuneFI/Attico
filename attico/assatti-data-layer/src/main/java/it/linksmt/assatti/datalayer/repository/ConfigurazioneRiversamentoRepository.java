package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneRiversamento;


/**
 * Spring Data JPA repository for the ConfigurazioneRiversamento entity.
 */
public interface ConfigurazioneRiversamentoRepository extends JpaRepository<ConfigurazioneRiversamento,Long>, QueryDslPredicateExecutor<ConfigurazioneRiversamento> {

	@Query(value = "SELECT * FROM configurazione_riversamento " + 
			"WHERE tipodocumento_id=:idTipoDocumento " + 
			"AND (tipoatto_id=0 OR tipoatto_id IS NULL OR tipoatto_id=:idTipoAtto) " + 
			"AND (aoo_id IS NULL OR aoo_id=0 OR aoo_id=:idAoo) " + 
			"AND validoal IS NULL " + 
			"ORDER BY tipoatto_id DESC, aoo_id DESC " + 
			"LIMIT 1" + 
			";"
			, nativeQuery = true
	)
	ConfigurazioneRiversamento find(@Param("idTipoDocumento") Long idTipoDocumento, @Param("idTipoAtto") Long idTipoAtto, @Param("idAoo") Long idAoo);

	@Query(value = "SELECT * FROM configurazione_riversamento " + 
			"WHERE tipodocumento_id=:idTipoDocumento " + 
			"AND ((:idTipoAtto is null and tipoatto_id is null) or (tipoatto_id = :idTipoAtto)) " + 
			"AND ((:idAoo is null and aoo_id is null) or (aoo_id = :idAoo)) " + 
			"AND validoal IS NULL " + 
			"ORDER BY tipoatto_id DESC, aoo_id DESC " + 
			"LIMIT 1" + 
			";"
			, nativeQuery = true
	)
	ConfigurazioneRiversamento findActive(@Param("idTipoDocumento") Long idTipoDocumento, @Param("idTipoAtto") Long idTipoAtto, @Param("idAoo") Long idAoo);

}

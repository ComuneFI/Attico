package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Classificazione;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerie;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Spring Data JPA repository for the Classificazione entity.
 */
public interface ClassificazioneRepository extends JpaRepository<Classificazione,Long>, QueryDslPredicateExecutor<Classificazione> {

	List<Classificazione> findByTipoDocumentoSerieAndAooAndValidoAlIsNull(TipoDocumentoSerie tipoDocumentoSerie, Aoo aooSerie);

	List<Classificazione> findByTipoDocumentoSerieAndAooIsNullAndValidoAlIsNull(TipoDocumentoSerie tipoDocumentoSerie);

	List<Classificazione> findByTipoDocumentoSerieIsNullAndAooIsNullAndValidoAlIsNull();

	List<Classificazione> findByTipoDocumentoSerie(TipoDocumentoSerie tipoDocumentoSerie);

}

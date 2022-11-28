package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.News;
import it.linksmt.assatti.datalayer.domain.QTipoAdempimento;
import it.linksmt.assatti.datalayer.domain.TipoAdempimento;
import it.linksmt.assatti.datalayer.repository.TipoAdempimentoRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoAdempimentoService {
	private final Logger log = LoggerFactory.getLogger(TipoAdempimentoService.class);

	@Inject
	private TipoAdempimentoRepository tipoAdempimentoRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional(readOnly=true)
	public TipoAdempimento findByDescrizioneAndTipoIterId(String descrizione, Long tipoIterId){
		if(descrizione!=null && !descrizione.isEmpty() && tipoIterId!=null && tipoIterId > 0L){
			Query query = entityManager.createNativeQuery("select * from tipoadempimento where REPLACE(descrizione, ',', '') = '"+descrizione+"' and tipoiter_id = " + tipoIterId, TipoAdempimento.class);
			TipoAdempimento risultato = null;
			try{
				risultato = (TipoAdempimento)query.getSingleResult();
			}catch(NoResultException | NonUniqueResultException | IllegalStateException | QueryTimeoutException ex){
				log.error("Errore nella ricerca del tipoadempimento " + descrizione + " tipoiter " + tipoIterId);
			}
			return risultato;
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<TipoAdempimento>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoAdempimento = QTipoAdempimento.tipoAdempimento.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("tipoiter")!=null && !search.get("tipoiter").isJsonNull() && search.get("tipoiter").getAsString()!=null && !"".equals(search.get("tipoiter").getAsString())){
				predicateTipoAdempimento = predicateTipoAdempimento.and(QTipoAdempimento.tipoAdempimento.tipoiter.id.eq(search.get("tipoiter").getAsLong()));
			}
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateTipoAdempimento = predicateTipoAdempimento.and(QTipoAdempimento.tipoAdempimento.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		Page<TipoAdempimento> page = tipoAdempimentoRepository.findAll(predicateTipoAdempimento, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoAdempimentos", offset, limit);
        return new ResponseEntity<List<TipoAdempimento>>(page.getContent(), headers, HttpStatus.OK);
	}
	
}

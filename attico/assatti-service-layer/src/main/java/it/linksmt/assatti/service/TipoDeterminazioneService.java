package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.QProfilo;
import it.linksmt.assatti.datalayer.domain.QTipoAtto;
import it.linksmt.assatti.datalayer.domain.QTipoDeterminazione;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoDeterminazione;
import it.linksmt.assatti.datalayer.repository.TipoAttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoDeterminazioneRepository;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoDeterminazioneService {
	private final Logger log = LoggerFactory.getLogger(TipoDeterminazioneService.class);
	
	@Inject
	private TipoDeterminazioneRepository tipoDeterminazioneRepository;
	
	@Inject
	private TipoAttoRepository tipoAttoRepository;
	
	@Transactional(readOnly=true)
	public TipoDeterminazione findByDescrizione(String descrizione){
		if(descrizione!=null && !descrizione.isEmpty()){
			return tipoDeterminazioneRepository.findOne(QTipoDeterminazione.tipoDeterminazione.descrizione.eq(descrizione));
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<TipoDeterminazione>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoDeterminazione = QTipoDeterminazione.tipoDeterminazione.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateTipoDeterminazione = predicateTipoDeterminazione.and(QTipoDeterminazione.tipoDeterminazione.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		if(search!=null && !search.isJsonNull()){
			if(search.get("statoTrasparenza")!=null && !search.get("statoTrasparenza").isJsonNull() && search.get("statoTrasparenza").getAsString()!=null && !"".equals(search.get("statoTrasparenza").getAsString())){
				predicateTipoDeterminazione = predicateTipoDeterminazione.and(QTipoDeterminazione.tipoDeterminazione.statoTrasparenza.containsIgnoreCase(search.get("statoTrasparenza").getAsString()));
			}
		}
		if(search.get("fileVisibiliInTrasparenza")!=null && !search.get("fileVisibiliInTrasparenza").isJsonNull()){
			predicateTipoDeterminazione = predicateTipoDeterminazione.and(QTipoDeterminazione.tipoDeterminazione.fileVisibiliInTrasparenza.eq(search.get("fileVisibiliInTrasparenza").getAsBoolean()));
		}
		if(search.get("tipoAtto")!=null && !search.get("tipoAtto").isJsonNull() && search.get("tipoAtto").getAsString()!=null && !"".equals(search.get("tipoAtto").getAsString())){
			BooleanExpression predicateTipiAtto =  QTipoAtto.tipoAtto.descrizione.containsIgnoreCase(search.get("tipoAtto").getAsString().trim());
			Iterable<TipoAtto> tipiAtto = tipoAttoRepository.findAll(predicateTipiAtto, new OrderSpecifier<>(Order.ASC, QTipoAtto.tipoAtto.id));
			BooleanExpression internalPredicate = null;
			for(TipoAtto q : tipiAtto){
				if(internalPredicate == null){
					internalPredicate = QTipoDeterminazione.tipoDeterminazione.tipiAtto.contains(q);
				}else{
					internalPredicate = internalPredicate.or(QTipoDeterminazione.tipoDeterminazione.tipiAtto.contains(q));
				}
			}
			if(internalPredicate!=null){
				predicateTipoDeterminazione = predicateTipoDeterminazione.and(internalPredicate);
			}
		}
		
		Page<TipoDeterminazione> page = tipoDeterminazioneRepository.findAll(predicateTipoDeterminazione, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoDeterminaziones", offset, limit);
        return new ResponseEntity<List<TipoDeterminazione>>(page.getContent(), headers, HttpStatus.OK);
	}
	
}

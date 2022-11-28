package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.QTipoIter;
import it.linksmt.assatti.datalayer.domain.TipoAdempimento;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoIter;
import it.linksmt.assatti.datalayer.repository.TipoIterRepository;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoIterService {
	private final Logger log = LoggerFactory.getLogger(TipoIterService.class);

	@Inject
	private TipoIterRepository tipoIterRepository;

	@Transactional(readOnly=true)
	public ResponseEntity<List<TipoIter>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoIter = QTipoIter.tipoIter.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("codice")!=null && !search.get("codice").isJsonNull() && search.get("codice").getAsString()!=null && !"".equals(search.get("codice").getAsString())){
				predicateTipoIter = predicateTipoIter.and(QTipoIter.tipoIter.codice.containsIgnoreCase(search.get("codice").getAsString()));
			}
		}
		if(search!=null && !search.isJsonNull()){
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateTipoIter = predicateTipoIter.and(QTipoIter.tipoIter.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		Page<TipoIter> page = tipoIterRepository.findAll(predicateTipoIter, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoIters", offset, limit);
        return new ResponseEntity<List<TipoIter>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional(readOnly = true)
	public TipoIter findByCodiceAndTipoAttoId(final String codice, final Long tipoAttoId) {
		if(codice!=null && !codice.isEmpty() && tipoAttoId!=null && tipoAttoId > 0L){
			return tipoIterRepository.findOne(QTipoIter.tipoIter.codice.eq(codice).and(QTipoIter.tipoIter.tipoAtto.id.eq(tipoAttoId)));
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public  TipoIter findOne(final Long id) {
		log.debug("findOne id" + id);
		TipoIter domain = tipoIterRepository.findOne(id);
		if(domain != null ){
			if( domain.getTipoAtto() != null ){
				domain.setTipoAtto( new TipoAtto(domain.getTipoAtto().getId(),domain.getTipoAtto().getCodice(),domain.getTipoAtto().getDescrizione()));
			}
		}
		return domain;
	}

	@Transactional
	public  void save(final TipoIter tipoiter) {
		log.debug("save" + tipoiter);
		if(tipoiter != null ){
			tipoIterRepository.save(tipoiter);
		}
	}

	@Transactional(readOnly=true)
	public Page<TipoIter> findAll(final Pageable generatePageRequest) {
		Page<TipoIter> l = tipoIterRepository.findAll(generatePageRequest);
		for (TipoIter tipo : l) {
			if( tipo.getTipoAtto() != null ){
				tipo.setTipoAtto( new TipoAtto(tipo.getTipoAtto().getId(),tipo.getTipoAtto().getCodice(),tipo.getTipoAtto().getDescrizione()));
			}
		}
		return l;
	}
	
	@Transactional(readOnly=true)
	public List<TipoIter> getByCodiceTipoAtto(final String codiceTipoAtto) {
		Iterable<TipoIter> it = tipoIterRepository.findAll(QTipoIter.tipoIter.tipoAtto.codice.eq(codiceTipoAtto));
		List<TipoIter> l = Lists.newArrayList(it);
		for (TipoIter tipo : l) {
			if( tipo.getTipoAtto() != null ){
				tipo.setTipoAtto( new TipoAtto(tipo.getTipoAtto().getId(),tipo.getTipoAtto().getCodice(),tipo.getTipoAtto().getDescrizione()));
			}
			tipo.setTipiAdempimenti(null);
		}
		return l;
	}

	@Transactional(readOnly=true)
	public List<TipoIter> getByTipoAtto(final Long tipoAttoId) {
		List<TipoIter> l= tipoIterRepository.findByTipoAttoId( tipoAttoId);

		for (TipoIter tipo : l) {
			if( tipo.getTipoAtto() != null ){
				tipo.setTipoAtto( new TipoAtto(tipo.getTipoAtto().getId(),tipo.getTipoAtto().getCodice(),tipo.getTipoAtto().getDescrizione()));
			}
			if(tipo.getTipiAdempimenti() != null){
				for (TipoAdempimento ademp: tipo.getTipiAdempimenti()  ) {
					ademp.getDescrizione();
					ademp.setTipoiter( new TipoIter(tipo.getId(), null, null) );
				}
			}
		}
		return l;
	}

}

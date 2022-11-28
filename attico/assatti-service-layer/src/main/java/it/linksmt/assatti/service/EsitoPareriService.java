package it.linksmt.assatti.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.EsitoPareri;
import it.linksmt.assatti.datalayer.domain.QEsitoPareri;
import it.linksmt.assatti.datalayer.repository.EsitoPareriRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class EsitoPareriService {
	private final Logger log = LoggerFactory.getLogger(EsitoPareriService.class);

	@Inject
	private EsitoPareriRepository esitoPareriRepository;
	
	@Transactional
	public void disable(Long id) {
		esitoPareriRepository.enableDisable(false, id);
	}
	
	@Transactional
	public void enable(Long id) {
		esitoPareriRepository.enableDisable(true, id);
	}
	
	@Transactional(readOnly = true)
	public List<EsitoPareri> findAllEnabled(){
		BooleanExpression predicate = QEsitoPareri.esitoPareri.enabled.eq(true);
		return Lists.newArrayList(esitoPareriRepository.findAll(predicate));
	}
	
	@Transactional(readOnly = true)
	public Map<String, String> findAllMap(){
		Map<String, String> map = new HashMap<String, String>();
		BooleanExpression predicate = QEsitoPareri.esitoPareri.enabled.eq(true);
		Iterable<EsitoPareri> it = esitoPareriRepository.findAll(predicate);
		if(it!=null) {
			for(EsitoPareri es : it) {
				if(es!=null && es.getCodice()!=null && !es.getCodice().isEmpty() && es.getValore()!=null && !es.getValore().isEmpty()) {
					map.put(es.getCodice(), es.getValore());
				}
			}
		}
		predicate = QEsitoPareri.esitoPareri.enabled.isNull().or(QEsitoPareri.esitoPareri.enabled.eq(false));
		it = esitoPareriRepository.findAll(predicate);
		if(it!=null) {
			for(EsitoPareri es : it) {
				if(es!=null && es.getCodice()!=null && !es.getCodice().isEmpty() && es.getValore()!=null && !es.getValore().isEmpty()) {
					if(!map.containsKey(es.getCodice())) {
						map.put(es.getCodice(), es.getValore());
					}
				}
			}
		}
		return map;
	}
	
	@Transactional(readOnly = true)
	public List<EsitoPareri> findByCodice(String codice){
		return esitoPareriRepository.findByCodice(codice);
	}

	@Transactional(readOnly = true)
	public  EsitoPareri findOne(Long id) {
		log.debug("findOne id" + id);
		EsitoPareri domain = esitoPareriRepository.findOne(id);

		return domain;
	}
	
	@Transactional(readOnly = true)
	public String getDescrizioneByCodiceEsitoPareri(String codiceEsitoPareri){
		return esitoPareriRepository.getValoreByCodiceEsitoPareri(codiceEsitoPareri);
	}
}

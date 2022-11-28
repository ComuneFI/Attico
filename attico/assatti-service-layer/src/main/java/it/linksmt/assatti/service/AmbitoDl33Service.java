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

import it.linksmt.assatti.datalayer.domain.AmbitoDl33;
import it.linksmt.assatti.datalayer.domain.QAmbitoDl33;
import it.linksmt.assatti.datalayer.repository.AmbitoDl33Repository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AmbitoDl33Service {
	private final Logger log = LoggerFactory.getLogger(AmbitoDl33Service.class);

	@Inject
	private AmbitoDl33Repository ambitoDl33Repository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<AmbitoDl33>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateAmbitoDl33 = QAmbitoDl33.ambitoDl33.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("denominazione")!=null && !search.get("denominazione").isJsonNull() && search.get("denominazione").getAsString()!=null && !"".equals(search.get("denominazione").getAsString())){
				predicateAmbitoDl33 = predicateAmbitoDl33.and(QAmbitoDl33.ambitoDl33.denominazione.containsIgnoreCase(search.get("denominazione").getAsString()));
			}
			if(search.get("attivo")!=null && !search.get("attivo").isJsonNull() && search.get("attivo").getAsString()!=null && !"".equals(search.get("attivo").getAsString())){
				predicateAmbitoDl33 = predicateAmbitoDl33.and(QAmbitoDl33.ambitoDl33.attivo.eq(search.get("attivo").getAsBoolean()));
			}
		}
		Page<AmbitoDl33> page = ambitoDl33Repository.findAll(predicateAmbitoDl33, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ambitoDl33s", offset, limit);
        return new ResponseEntity<List<AmbitoDl33>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	
}

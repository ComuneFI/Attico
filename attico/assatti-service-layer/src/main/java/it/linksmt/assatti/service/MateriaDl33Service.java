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

import it.linksmt.assatti.datalayer.domain.MateriaDl33;
import it.linksmt.assatti.datalayer.domain.QMateriaDl33;
import it.linksmt.assatti.datalayer.repository.MateriaDl33Repository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class MateriaDl33Service {
	private final Logger log = LoggerFactory.getLogger(MateriaDl33Service.class);
	
	@Inject
	private MateriaDl33Repository materiaDl33Repository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<MateriaDl33>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateMateriaDl33 = QMateriaDl33.materiaDl33.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("denominazione")!=null && !search.get("denominazione").isJsonNull() && search.get("denominazione").getAsString()!=null && !"".equals(search.get("denominazione").getAsString())){
				predicateMateriaDl33 = predicateMateriaDl33.and(QMateriaDl33.materiaDl33.denominazione.containsIgnoreCase(search.get("denominazione").getAsString()));
			}
			if(search.get("attivo")!=null && !search.get("attivo").isJsonNull() && search.get("attivo").getAsString()!=null && !"".equals(search.get("attivo").getAsString())){
				predicateMateriaDl33 = predicateMateriaDl33.and(QMateriaDl33.materiaDl33.attivo.eq(search.get("attivo").getAsBoolean()));
			}
			if(search.get("ambitoDl33")!=null && !search.get("ambitoDl33").isJsonNull() && search.get("ambitoDl33").getAsString()!=null && !"".equals(search.get("ambitoDl33").getAsString())){
				predicateMateriaDl33 = predicateMateriaDl33.and(QMateriaDl33.materiaDl33.ambitoDl33.id.eq(search.get("ambitoDl33").getAsLong()));
			}
		}
		Page<MateriaDl33> page = materiaDl33Repository.findAll(predicateMateriaDl33, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/materiaDl33s", offset, limit);
        return new ResponseEntity<List<MateriaDl33>>(page.getContent(), headers, HttpStatus.OK);
	}
	
}

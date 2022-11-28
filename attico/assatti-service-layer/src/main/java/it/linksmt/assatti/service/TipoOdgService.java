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

import it.linksmt.assatti.datalayer.domain.QTipoOdg;
import it.linksmt.assatti.datalayer.domain.TipoOdg;
import it.linksmt.assatti.datalayer.repository.TipoOdgRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoOdgService {
	private final Logger log = LoggerFactory.getLogger(TipoOdgService.class);

	@Inject
	private TipoOdgRepository tipoOdgRepository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<TipoOdg>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoOdg = QTipoOdg.tipoOdg.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateTipoOdg = predicateTipoOdg.and(QTipoOdg.tipoOdg.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		Page<TipoOdg> page = tipoOdgRepository.findAll(predicateTipoOdg, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoOdgs", offset, limit);
        return new ResponseEntity<List<TipoOdg>>(page.getContent(), headers, HttpStatus.OK);
	}
}

package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Annullamento;
import it.linksmt.assatti.datalayer.domain.QTipoAtto;
import it.linksmt.assatti.datalayer.domain.QTipoProgressivo;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoProgressivo;
import it.linksmt.assatti.datalayer.repository.TipoProgressivoRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoProgressivoService {
	private final Logger log = LoggerFactory.getLogger(TipoProgressivoService.class);

	@Inject
	private TipoProgressivoRepository tipoProgressivoRepository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<TipoProgressivo>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoProgressivo = QTipoProgressivo.tipoProgressivo.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateTipoProgressivo = predicateTipoProgressivo.and(QTipoProgressivo.tipoProgressivo.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		Sort sort = new Sort(new org.springframework.data.domain.Sort.Order(Direction.ASC, "descrizione" ));
		Page<TipoProgressivo> page = tipoProgressivoRepository.findAll(predicateTipoProgressivo, PaginationUtil.generatePageRequest(offset, limit, sort));

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoProgressivos", offset, limit);
        return new ResponseEntity<List<TipoProgressivo>>(page.getContent(), headers, HttpStatus.OK);
	}
}

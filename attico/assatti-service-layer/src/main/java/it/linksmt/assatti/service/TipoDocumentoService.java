package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QTipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.repository.TipoDocumentoRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoDocumentoService {
	private final Logger log = LoggerFactory.getLogger(TipoDocumentoService.class);

	@Inject
	private TipoDocumentoRepository tipoDocumentoRepository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<TipoDocumento>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoDocumento = QTipoDocumento.tipoDocumento.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateTipoDocumento = predicateTipoDocumento.and(QTipoDocumento.tipoDocumento.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
			if(search.get("codice")!=null && !search.get("codice").isJsonNull() && search.get("codice").getAsString()!=null && !"".equals(search.get("codice").getAsString())){
				predicateTipoDocumento = predicateTipoDocumento.and(QTipoDocumento.tipoDocumento.codice.containsIgnoreCase(search.get("codice").getAsString()));
			}
			if(search.get("riversamentoTipoatto")!=null && !search.get("riversamentoTipoatto").isJsonNull()){
				predicateTipoDocumento = predicateTipoDocumento.and(QTipoDocumento.tipoDocumento.riversamentoTipoatto.eq(search.get("riversamentoTipoatto").getAsBoolean()));
			}
		}
		Page<TipoDocumento> page = tipoDocumentoRepository.findAll(predicateTipoDocumento, PaginationUtil.generatePageRequest(offset, limit, new Sort(new Order(Direction.ASC, "descrizione"))));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoDocumentos", offset, limit);
        return new ResponseEntity<List<TipoDocumento>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional(readOnly=true)
	public TipoDocumento findByCodice(String codice){
		return tipoDocumentoRepository.findByCodice(codice);
	}
	
	@Transactional(readOnly=true)
	public Integer countTipoDocumentoByCodice(String codice, Long id){
		if(id==null){
			return tipoDocumentoRepository.countTipoDocumentoByCodice(codice);
		}else{
			return tipoDocumentoRepository.countTipoDocumentoByCodiceButId(codice, id);
		}
	}
	
}

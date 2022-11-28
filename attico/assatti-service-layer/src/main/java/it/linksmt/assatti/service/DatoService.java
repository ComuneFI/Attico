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

import it.linksmt.assatti.datalayer.domain.Dato;
import it.linksmt.assatti.datalayer.domain.QDato;
import it.linksmt.assatti.datalayer.domain.TipoDatoEnum;
import it.linksmt.assatti.datalayer.repository.DatoRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class DatoService {
	private final Logger log = LoggerFactory.getLogger(DatoService.class);

	@Inject
	private DatoRepository datoRepository;

	@Transactional(readOnly=true)
	public ResponseEntity<List<Dato>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateDato = QDato.dato.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("etichetta")!=null && !search.get("etichetta").isJsonNull() && search.get("etichetta").getAsString()!=null && !"".equals(search.get("etichetta").getAsString())){
				predicateDato = predicateDato.and(QDato.dato.etichetta.containsIgnoreCase(search.get("etichetta").getAsString()));
			}
			if(search.get("dato_tipdat_fk")!=null && !search.get("dato_tipdat_fk").isJsonNull() && search.get("dato_tipdat_fk").getAsString()!=null && !"".equals(search.get("dato_tipdat_fk").getAsString())){
				TipoDatoEnum tipoDatoToSearch = null;
				for(TipoDatoEnum tipoDato : TipoDatoEnum.values()){
					if(tipoDato.toString().equals(search.get("dato_tipdat_fk").getAsString())){
						tipoDatoToSearch = tipoDato;
						break;
					}
				}
				if(tipoDatoToSearch!=null){
					predicateDato = predicateDato.and(QDato.dato.tipoDato.eq(tipoDatoToSearch));
				}else{
					predicateDato = predicateDato.and(QDato.dato.id.isNull());
				}
			}
			if(search.get("multivalore")!=null && !search.get("multivalore").isJsonNull() && search.get("multivalore").getAsString()!=null && !"".equals(search.get("multivalore").getAsString())){
				predicateDato = predicateDato.and(QDato.dato.multivalore.containsIgnoreCase(search.get("multivalore").getAsString()));
			}
		}
		Page<Dato> page = datoRepository.findAll(predicateDato, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/datos", offset, limit);
        return new ResponseEntity<List<Dato>>(page.getContent(), headers, HttpStatus.OK);
	}
	
}

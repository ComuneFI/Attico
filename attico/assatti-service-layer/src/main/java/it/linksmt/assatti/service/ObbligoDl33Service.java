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

import it.linksmt.assatti.datalayer.domain.Obbligo_DL33;
import it.linksmt.assatti.datalayer.domain.QObbligo_DL33;
import it.linksmt.assatti.datalayer.domain.Scheda;
import it.linksmt.assatti.datalayer.repository.Obbligo_DL33Repository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ObbligoDl33Service {
	private final Logger log = LoggerFactory.getLogger(ObbligoDl33Service.class);

	@Inject
	private Obbligo_DL33Repository obbligo_DL33Repository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<Obbligo_DL33>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateObbligoDl33 = QObbligo_DL33.obbligo_DL33.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("attiva")!=null && !search.get("attiva").isJsonNull() && search.get("attiva").getAsString()!=null && !"".equals(search.get("attiva").getAsString())){
				predicateObbligoDl33 = predicateObbligoDl33.and(QObbligo_DL33.obbligo_DL33.attivo.eq(search.get("attiva").getAsBoolean()));
			}
			if(search.get("codice")!=null && !search.get("codice").isJsonNull() && search.get("codice").getAsString()!=null && !"".equals(search.get("codice").getAsString())){
				predicateObbligoDl33 = predicateObbligoDl33.and(QObbligo_DL33.obbligo_DL33.codice.containsIgnoreCase(search.get("codice").getAsString()));
			}
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateObbligoDl33 = predicateObbligoDl33.and(QObbligo_DL33.obbligo_DL33.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
			if(search.get("cat_obbligo_DL33")!=null && !search.get("cat_obbligo_DL33").isJsonNull() && search.get("cat_obbligo_DL33").getAsString()!=null && !"".equals(search.get("cat_obbligo_DL33").getAsString())){
				predicateObbligoDl33 = predicateObbligoDl33.and(QObbligo_DL33.obbligo_DL33.cat_obbligo_DL33.id.eq(search.get("cat_obbligo_DL33").getAsLong()));
			}
			if(search.get("macro_cat_obbligo_DL33")!=null && !search.get("macro_cat_obbligo_DL33").isJsonNull() && search.get("macro_cat_obbligo_DL33").getAsString()!=null && !"".equals(search.get("macro_cat_obbligo_DL33").getAsString())){
				predicateObbligoDl33 = predicateObbligoDl33.and(QObbligo_DL33.obbligo_DL33.cat_obbligo_DL33.fk_cat_obbligo_macro_cat_obbligo_idx.id.eq(search.get("macro_cat_obbligo_DL33").getAsLong()));
			}
			if(search.get("schede")!=null && !search.get("schede").isJsonNull() && search.get("schede").getAsString()!=null && !"".equals(search.get("schede").getAsString())){
				if(search.get("schede").getAsString().equals("no")){
					predicateObbligoDl33 = predicateObbligoDl33.and(QObbligo_DL33.obbligo_DL33.schedas.isEmpty());
				}else if(search.get("schede").getAsString().equals("si")){
					predicateObbligoDl33 = predicateObbligoDl33.and(QObbligo_DL33.obbligo_DL33.schedas.isNotEmpty());
				}
			}
		}
		Page<Obbligo_DL33> page = obbligo_DL33Repository.findAll(predicateObbligoDl33, PaginationUtil.generatePageRequest(offset, limit));
		for(Obbligo_DL33 obb : page.getContent()){
			for(Scheda scheda : obb.getSchedas()){
				scheda.getId(); //get lazy info about schede
			}
		}
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/obbligo_DL33s", offset, limit);
        return new ResponseEntity<List<Obbligo_DL33>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional
	public void abilitaObbligo(final Long id){
		obbligo_DL33Repository.abilitaObbligo(id);
	}
	
	@Transactional
	public void disabilitaObbligo(final Long id){
		obbligo_DL33Repository.disabilitaObbligo(id);
	}
	
	@Transactional(readOnly=true)
	public boolean checkIfAlreadyExists(final Long obblId, final Long catId, final String codice){
		BooleanExpression predicate = QObbligo_DL33.obbligo_DL33.id.isNotNull();
		predicate = predicate.and(QObbligo_DL33.obbligo_DL33.codice.eq(codice));
		predicate = predicate.and(QObbligo_DL33.obbligo_DL33.cat_obbligo_DL33.id.eq(catId));
		if(obblId!=null && obblId.longValue()>0){
			predicate = predicate.and(QObbligo_DL33.obbligo_DL33.id.ne(obblId));
		}
		if(obbligo_DL33Repository.count(predicate) > 0){
			return true;
		}else{
			return false;
		}
	}
	
}

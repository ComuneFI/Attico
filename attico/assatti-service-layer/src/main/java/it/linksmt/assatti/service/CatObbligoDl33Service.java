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

import it.linksmt.assatti.datalayer.domain.Cat_obbligo_dl33;
import it.linksmt.assatti.datalayer.domain.QCat_obbligo_dl33;
import it.linksmt.assatti.datalayer.repository.Cat_obbligo_DL33Repository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class CatObbligoDl33Service {
	private final Logger log = LoggerFactory.getLogger(CatObbligoDl33Service.class);

	@Inject
	private Cat_obbligo_DL33Repository cat_obbligo_DL33Repository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<Cat_obbligo_dl33>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateCatObbligoDl33 = QCat_obbligo_dl33.cat_obbligo_dl33.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			
			if(search.get("fk_cat_obbligo_macro_cat_obbligo_idx")!=null && !search.get("fk_cat_obbligo_macro_cat_obbligo_idx").isJsonNull() && search.get("fk_cat_obbligo_macro_cat_obbligo_idx").getAsString()!=null && !"".equals(search.get("fk_cat_obbligo_macro_cat_obbligo_idx").getAsString())){
				predicateCatObbligoDl33 = predicateCatObbligoDl33.and(QCat_obbligo_dl33.cat_obbligo_dl33.fk_cat_obbligo_macro_cat_obbligo_idx.id.eq(search.get("fk_cat_obbligo_macro_cat_obbligo_idx").getAsLong()));
			}
			if(search.get("attiva")!=null && !search.get("attiva").isJsonNull() && search.get("attiva").getAsString()!=null && !"".equals(search.get("attiva").getAsString())){
				predicateCatObbligoDl33 = predicateCatObbligoDl33.and(QCat_obbligo_dl33.cat_obbligo_dl33.attiva.eq(search.get("attiva").getAsBoolean()));
			}
			if(search.get("codice")!=null && !search.get("codice").isJsonNull() && search.get("codice").getAsString()!=null && !"".equals(search.get("codice").getAsString())){
				predicateCatObbligoDl33 = predicateCatObbligoDl33.and(QCat_obbligo_dl33.cat_obbligo_dl33.codice.containsIgnoreCase(search.get("codice").getAsString()));
			}
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateCatObbligoDl33 = predicateCatObbligoDl33.and(QCat_obbligo_dl33.cat_obbligo_dl33.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		Page<Cat_obbligo_dl33> page = cat_obbligo_DL33Repository.findAll(predicateCatObbligoDl33, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cat_obbligo_DL33s", offset, limit);
        return new ResponseEntity<List<Cat_obbligo_dl33>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional
	public void abilitaCategoria(Long id){
		cat_obbligo_DL33Repository.abilitaCategoria(id);
	}
	
	@Transactional
	public void disabilitaCategoria(Long id){
		cat_obbligo_DL33Repository.disabilitaCategoria(id);
	}	
	
	@Transactional(readOnly=true)
	public boolean checkIfAlreadyExists(final Long catId, final Long macroId, final String codice){
		BooleanExpression predicate = QCat_obbligo_dl33.cat_obbligo_dl33.id.isNotNull();
		predicate = predicate.and(QCat_obbligo_dl33.cat_obbligo_dl33.codice.eq(codice));
		predicate = predicate.and(QCat_obbligo_dl33.cat_obbligo_dl33.fk_cat_obbligo_macro_cat_obbligo_idx.id.eq(macroId));
		if(catId!=null && catId.longValue()>0){
			predicate = predicate.and(QCat_obbligo_dl33.cat_obbligo_dl33.id.ne(catId));
		}
		if(cat_obbligo_DL33Repository.count(predicate) > 0){
			return true;
		}else{
			return false;
		}
	}
}

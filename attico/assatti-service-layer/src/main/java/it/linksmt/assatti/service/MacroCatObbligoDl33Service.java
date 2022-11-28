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

import it.linksmt.assatti.datalayer.domain.Macro_cat_obbligo_dl33;
import it.linksmt.assatti.datalayer.domain.QMacro_cat_obbligo_dl33;
import it.linksmt.assatti.datalayer.domain.QTipoAtto;
import it.linksmt.assatti.datalayer.repository.Macro_cat_obbligo_dl33Repository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class MacroCatObbligoDl33Service {
	private final Logger log = LoggerFactory.getLogger(MacroCatObbligoDl33Service.class);

	@Inject
	private Macro_cat_obbligo_dl33Repository macro_cat_obbligo_dl33Repository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<Macro_cat_obbligo_dl33>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateMacroCatObbligoDl33 = QMacro_cat_obbligo_dl33.macro_cat_obbligo_dl33.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("attiva")!=null && !search.get("attiva").isJsonNull() && search.get("attiva").getAsString()!=null && !"".equals(search.get("attiva").getAsString())){
				predicateMacroCatObbligoDl33 = predicateMacroCatObbligoDl33.and(QMacro_cat_obbligo_dl33.macro_cat_obbligo_dl33.attiva.eq(search.get("attiva").getAsBoolean()));
			}
			if(search.get("codice")!=null && !search.get("codice").isJsonNull() && search.get("codice").getAsString()!=null && !"".equals(search.get("codice").getAsString())){
				predicateMacroCatObbligoDl33 = predicateMacroCatObbligoDl33.and(QMacro_cat_obbligo_dl33.macro_cat_obbligo_dl33.codice.containsIgnoreCase(search.get("codice").getAsString()));
			}
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateMacroCatObbligoDl33 = predicateMacroCatObbligoDl33.and(QMacro_cat_obbligo_dl33.macro_cat_obbligo_dl33.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
		}
		Page<Macro_cat_obbligo_dl33> page = macro_cat_obbligo_dl33Repository.findAll(predicateMacroCatObbligoDl33, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/macro_cat_obbligo_dl33s", offset, limit);
        return new ResponseEntity<List<Macro_cat_obbligo_dl33>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional
	public void abilitaMacroCategoria(final Long id){
		macro_cat_obbligo_dl33Repository.abilitaMacroCategoria(id);
	}
	
	@Transactional
	public void disabilitaMacroCategoria(final Long id){
		macro_cat_obbligo_dl33Repository.disabilitaMacroCategoria(id);
	}
	
	@Transactional(readOnly=true)
	public boolean checkIfAlreadyExists(final Long macroId, final String codice){
		BooleanExpression predicate = QMacro_cat_obbligo_dl33.macro_cat_obbligo_dl33.id.isNotNull();
		predicate = predicate.and(QMacro_cat_obbligo_dl33.macro_cat_obbligo_dl33.codice.eq(codice));
		if(macroId!=null && macroId.longValue()>0){
			predicate = predicate.and(QMacro_cat_obbligo_dl33.macro_cat_obbligo_dl33.id.ne(macroId));
		}
		if(macro_cat_obbligo_dl33Repository.count(predicate) > 0){
			return true;
		}else{
			return false;
		}
	}
}

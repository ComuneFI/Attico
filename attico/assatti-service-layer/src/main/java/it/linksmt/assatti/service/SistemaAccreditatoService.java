package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QSistemaAccreditato;
import it.linksmt.assatti.datalayer.domain.SistemaAccreditato;
import it.linksmt.assatti.datalayer.repository.SistemaAccreditatoRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class SistemaAccreditatoService {
	private final Logger log = LoggerFactory.getLogger(SistemaAccreditatoService.class);

	@Inject
	private SistemaAccreditatoRepository sistemaAccreditatoRepository;
	
	@Transactional(readOnly = true)
	public SistemaAccreditato findByCodice(String codice){
		if(codice!=null && !codice.isEmpty()){
			return sistemaAccreditatoRepository.findOne(QSistemaAccreditato.sistemaAccreditato.codice.eq(codice));
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public Page<SistemaAccreditato> findbyCriteria(JsonObject criteria, Integer limit, Integer offset){
		BooleanExpression predicate = QSistemaAccreditato.sistemaAccreditato.id.isNotNull(); 
		if(criteria!=null && !criteria.isJsonNull()){
			if(criteria.has("codice") && !criteria.get("codice").getAsString().isEmpty()){
				predicate = predicate.and(QSistemaAccreditato.sistemaAccreditato.codice.like("%" + criteria.get("codice").getAsString() + "%"));
			}
			if(criteria.has("descrizione") && !criteria.get("descrizione").getAsString().isEmpty()){
				predicate = predicate.and(QSistemaAccreditato.sistemaAccreditato.descrizione.like("%" + criteria.get("descrizione").getAsString() + "%"));
			}
			if(criteria.has("enabled") && !criteria.get("enabled").isJsonNull() && !criteria.get("enabled").getAsString().isEmpty()){
				predicate = predicate.and(QSistemaAccreditato.sistemaAccreditato.enabled.eq(criteria.get("enabled").getAsBoolean()));
			}
		}
		return sistemaAccreditatoRepository.findAll(predicate, PaginationUtil.generatePageRequest(offset, limit));
	}

	@Transactional
	public void createUpdate(SistemaAccreditato sistemaAccreditato){
		sistemaAccreditatoRepository.save(sistemaAccreditato);
	}
	
}

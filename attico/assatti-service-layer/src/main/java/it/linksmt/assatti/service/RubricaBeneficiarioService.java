package it.linksmt.assatti.service;

import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.QRubricaBeneficiario;
import it.linksmt.assatti.datalayer.domain.RubricaBeneficiario;
import it.linksmt.assatti.datalayer.repository.RubricaBeneficiarioRepository;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class RubricaBeneficiarioService {

	private final Logger log = LoggerFactory.getLogger(RubricaBeneficiarioService.class);

	@Inject
	private RubricaBeneficiarioRepository rubricaBeneficiarioRepository;

	@Transactional(readOnly=true)
	public Page<RubricaBeneficiario> search(JsonObject search, Pageable paginazione){
		Page<RubricaBeneficiario> ritorno = null;
		if(search == null){
			ritorno = rubricaBeneficiarioRepository.findAll(QRubricaBeneficiario.rubricaBeneficiario.id.isNull(), paginazione);
		}else{
			BooleanExpression predicate = QRubricaBeneficiario.rubricaBeneficiario.id.isNotNull();
			if(search.has("stato") && search.getAsJsonObject("stato").has("id") && !search.getAsJsonObject("stato").get("id").getAsString().trim().equalsIgnoreCase("")){
				if("0".equals(search.getAsJsonObject("stato").get("id").getAsString().trim())){ //attivi
					predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.attivo.eq(true));
				}else if("1".equals(search.getAsJsonObject("stato").get("id").getAsString().trim())){ //disattivi
					predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.attivo.eq(false));
				}
			}
			if(search.has("nome") && !search.get("nome").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.nome.containsIgnoreCase(search.get("nome").getAsString().trim()));
			}
			if(search.has("cognome") && !search.get("cognome").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.cognome.containsIgnoreCase(search.get("cognome").getAsString().trim()));
			}
			if(search.has("denominazione") && !search.get("denominazione").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.denominazione.containsIgnoreCase(search.get("denominazione").getAsString().trim()));
			}
			if(search.has("codiceFiscale") && !search.get("codiceFiscale").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.codiceFiscale.containsIgnoreCase(search.get("codiceFiscale").getAsString().trim()));
			}
			if(search.has("partitaIva") && !search.get("partitaIva").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.partitaIva.containsIgnoreCase(search.get("partitaIva").getAsString().trim()));
			}
			if(search.has("aooSearch") && !search.getAsJsonObject("aooSearch").isJsonNull() && search.getAsJsonObject("aooSearch").has("id") && !search.getAsJsonObject("aooSearch").get("id").getAsString().trim().equalsIgnoreCase("")){
				if(search.getAsJsonObject("aooSearch").get("id").getAsString().trim().equals("0")){
					predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.aoo.isNull());
				}else{
					predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.aoo.id.eq(Long.parseLong(search.getAsJsonObject("aooSearch").get("id").getAsString().trim())));
				}
			}else if(search.has("aoo") && !search.getAsJsonObject("aoo").isJsonNull() && search.getAsJsonObject("aoo").has("id") && !search.getAsJsonObject("aoo").get("id").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaBeneficiario.rubricaBeneficiario.aoo.id.eq(Long.parseLong(search.getAsJsonObject("aoo").get("id").getAsString().trim())).or(QRubricaBeneficiario.rubricaBeneficiario.aoo.isNull()));
			}
			ritorno = rubricaBeneficiarioRepository.findAll(predicate, paginazione);
		}
		for(RubricaBeneficiario rb : ritorno.getContent()){
			rb.setAoo( DomainUtil.minimalAoo(rb.getAoo()));
		}
		return ritorno;
	}
	
	
	@Transactional(readOnly=true)
	public Page<RubricaBeneficiario> findByAooIdAndValidi(Long aooId, Pageable generatePageRequest) {
		log.debug("findByAooIdAndValidi aooId:"+aooId);
		BooleanExpression predicateRubrica = QRubricaBeneficiario.rubricaBeneficiario.aoo.id.eq( aooId).or(QRubricaBeneficiario.rubricaBeneficiario.aoo.id.isNull());
		predicateRubrica = predicateRubrica.and(QRubricaBeneficiario.rubricaBeneficiario.attivo.eq(true));
		Page<RubricaBeneficiario> beneficiari = rubricaBeneficiarioRepository.findAll(predicateRubrica, generatePageRequest);
		for (RubricaBeneficiario rubricaBeneficiario : beneficiari) {
			rubricaBeneficiario.setAoo( DomainUtil.minimalAoo(rubricaBeneficiario.getAoo()));
//			DomainUtil.nameCerca( rubricaBeneficiario );
		}
		return beneficiari;
	}
	
	@Transactional( readOnly=true )
	public Iterable<RubricaBeneficiario> findByAooIdAndValidi(Long aooId ) {
		log.debug("findByAooIdAndValidi aooId:"+aooId);
		BooleanExpression predicateRubrica = QRubricaBeneficiario.rubricaBeneficiario.aoo.id.eq( aooId);
		predicateRubrica = predicateRubrica.and(QRubricaBeneficiario.rubricaBeneficiario.attivo.eq(true));
		Iterable<RubricaBeneficiario> profili = rubricaBeneficiarioRepository.findAll(predicateRubrica);
		for (RubricaBeneficiario rubricaBeneficiario : profili) {
			rubricaBeneficiario.setAoo( DomainUtil.minimalAoo(rubricaBeneficiario.getAoo()));
//			DomainUtil.nameCerca( rubricaBeneficiario );
		}
		return profili;
	}

	@Transactional( readOnly=true )
	public Page<RubricaBeneficiario> findAll(Pageable generatePageRequest) {
		Page<RubricaBeneficiario> l = rubricaBeneficiarioRepository.findAll(generatePageRequest);
		for (RubricaBeneficiario rubricaBeneficiario : l) {
			rubricaBeneficiario.setAoo( DomainUtil.minimalAoo(rubricaBeneficiario.getAoo()));
//			DomainUtil.nameCerca( rubricaBeneficiario );
		}
		return l;
	}
	
	@Transactional(readOnly=true)
	public Iterable<RubricaBeneficiario> findByIds(Set<Long> beneficiariIds){
		BooleanExpression predicate = QRubricaBeneficiario.rubricaBeneficiario.id.in(beneficiariIds);
		return rubricaBeneficiarioRepository.findAll(predicate);
	}

	public RubricaBeneficiario save(RubricaBeneficiario beneficiario) {
		
		if(beneficiario.getAoo()!=null && beneficiario.getAoo().getId() == null) {
			beneficiario.setAoo(null);
		}
		return rubricaBeneficiarioRepository.save(beneficiario);
	}
	
	public void delete(Long id) {
		RubricaBeneficiario entity = rubricaBeneficiarioRepository.findOne(id);
		entity.setAttivo(false);
		rubricaBeneficiarioRepository.save( entity);
	}
	
	@Transactional(readOnly=false)
	public void disable(final Long id){
		log.debug("disable rubricaBeneficiario with id " + id);
		RubricaBeneficiario rubricaBeneficiario = rubricaBeneficiarioRepository.findOne(id);
		if(rubricaBeneficiario!=null){
			rubricaBeneficiario.setAttivo(false);
			rubricaBeneficiarioRepository.save(rubricaBeneficiario);
		}
	}
	
	@Transactional(readOnly=false)
	public void enable(final Long id){
		log.debug("enable rubricaBeneficiario with id " + id);
		RubricaBeneficiario rubricaBeneficiario = rubricaBeneficiarioRepository.findOne(id);
		if(rubricaBeneficiario!=null){
			rubricaBeneficiario.setAttivo(true);
			rubricaBeneficiarioRepository.save(rubricaBeneficiario);
		}
	}


	@Transactional(readOnly=true)
	public RubricaBeneficiario findOne(Long id) {
		RubricaBeneficiario rBenef = rubricaBeneficiarioRepository.findOne(id);
		rBenef.setAoo( DomainUtil.minimalAoo(rBenef.getAoo()));
		return rBenef;		 
	}
	
	
}

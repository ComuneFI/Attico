package it.linksmt.assatti.service;

import java.util.Calendar;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.QRubricaBeneficiario;
import it.linksmt.assatti.datalayer.domain.QRubricaDestinatarioEsterno;
import it.linksmt.assatti.datalayer.domain.RubricaDestinatarioEsterno;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.RubricaDestinatarioEsternoRepository;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class RubricaDestinatarioEsternoService {

	private final Logger log = LoggerFactory.getLogger(RubricaDestinatarioEsternoService.class);

	@Inject
	private RubricaDestinatarioEsternoRepository rubricaDestinatarioEsternoRepository;
	@Inject
	private ValiditaService validitaService;
	
	@Transactional(readOnly=true)
	public boolean checkIfAlreadyExists(RubricaDestinatarioEsterno rubricaDestinatarioEsterno) throws GestattiCatchedException{
		BooleanExpression predicate = QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.id.isNotNull();
		if(rubricaDestinatarioEsterno.getId()!=null){
			predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.id.notIn(rubricaDestinatarioEsterno.getId()));
		}
		if(rubricaDestinatarioEsterno.getAoo()!=null && rubricaDestinatarioEsterno.getAoo().getId()!=null){
			predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.id.eq(rubricaDestinatarioEsterno.getAoo().getId()));
		}else{
			predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.isNull());
		}
		if(rubricaDestinatarioEsterno.getPec()!=null && !rubricaDestinatarioEsterno.getPec().trim().isEmpty() && rubricaDestinatarioEsterno.getTipo().equalsIgnoreCase("privato") && rubricaDestinatarioEsterno.getNome()!=null && !rubricaDestinatarioEsterno.getNome().trim().isEmpty() && rubricaDestinatarioEsterno.getCognome()!=null && !rubricaDestinatarioEsterno.getCognome().trim().isEmpty()){
			predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.nome.eq(rubricaDestinatarioEsterno.getNome())).and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.cognome.eq(rubricaDestinatarioEsterno.getCognome())).and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.pec.eq(rubricaDestinatarioEsterno.getPec()));
		}else if(rubricaDestinatarioEsterno.getPec()!=null && !rubricaDestinatarioEsterno.getPec().trim().isEmpty() && rubricaDestinatarioEsterno.getTipo().equalsIgnoreCase("azienda") && rubricaDestinatarioEsterno.getDenominazione()!=null && !rubricaDestinatarioEsterno.getDenominazione().trim().isEmpty()){
			predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.denominazione.eq(rubricaDestinatarioEsterno.getDenominazione())).and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.pec.eq(rubricaDestinatarioEsterno.getPec()));
		}else{
			throw new GestattiCatchedException("Informazioni obbligatorie del destinatario mancanti.");
		}
		return rubricaDestinatarioEsternoRepository.count(predicate) > 0L;
	}

	@Transactional(readOnly=true)
	public Page<RubricaDestinatarioEsterno> search(JsonObject search, Pageable paginazione){
		if(search == null){
			return rubricaDestinatarioEsternoRepository.findAll(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.id.isNull(), paginazione);
		}else{
			BooleanExpression predicate = QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.id.isNotNull();
			if(search.has("stato") && search.getAsJsonObject("stato").has("id") && !search.getAsJsonObject("stato").get("id").getAsString().trim().equalsIgnoreCase("")){
				if("0".equals(search.getAsJsonObject("stato").get("id").getAsString().trim())){ //attivi
					predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.validita.validoal.isNull());
				}else if("1".equals(search.getAsJsonObject("stato").get("id").getAsString().trim())){ //disattivi
					predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.validita.validoal.isNotNull());
				}
			}
			if(search.has("notificaGiuntaAutomatica") && !search.get("notificaGiuntaAutomatica").isJsonNull() && !search.get("notificaGiuntaAutomatica").getAsString().isEmpty() && !search.get("notificaGiuntaAutomatica").getAsString().trim().equals("-")){
				if(search.get("notificaGiuntaAutomatica").getAsString().equals("true")){
					predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.notificaGiuntaAutomatica.eq(true));
				}else if(search.get("notificaGiuntaAutomatica").getAsString().equals("false")){
					predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.notificaGiuntaAutomatica.eq(false));
				}
			}
			if(search.has("tipo") && !search.get("tipo").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.tipo.containsIgnoreCase(search.get("tipo").getAsString().trim()));
			}
//			if(search.has("notificagiunta") && search.get("notificagiunta").getAsBoolean() == false){
//				log.debug("notificagiunta:",search.get("notificagiunta").getAsBoolean());
//				
//				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.notificaGiuntaAutomatica.ne(true));
//			}
			if(search.has("notificagiunta")){
				Boolean notificaGiuntaValue = search.get("notificagiunta").getAsBoolean();
				log.debug("notificagiunta:",notificaGiuntaValue.toString());
				
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.notificaGiuntaAutomatica.eq(notificaGiuntaValue));
			}
			if(search.has("denominazione") && !search.get("denominazione").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.denominazione.containsIgnoreCase(search.get("denominazione").getAsString().trim()));
			}
			if(search.has("nome") && !search.get("nome").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.nome.containsIgnoreCase(search.get("nome").getAsString().trim()));
			}
			if(search.has("cognome") && !search.get("cognome").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.cognome.containsIgnoreCase(search.get("cognome").getAsString().trim()));
			}
			if(search.has("email") && !search.get("email").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.email.containsIgnoreCase(search.get("email").getAsString().trim()));
			}
			if(search.has("pec") && !search.get("pec").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.pec.containsIgnoreCase(search.get("pec").getAsString().trim()));
			}
			
			if(search.has("aoo") && !search.getAsJsonObject("aoo").isJsonNull() && search.getAsJsonObject("aoo").has("id") && !search.getAsJsonObject("aoo").get("id").getAsString().trim().equalsIgnoreCase("")){
				if(search.getAsJsonObject("aoo").get("id").getAsString().trim().equals("0")){
					predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.isNull());
				}else{
					predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.id.eq(Long.parseLong(search.getAsJsonObject("aoo").get("id").getAsString().trim())));
				}
			}else if(search.has("aoo") && !search.getAsJsonObject("aoo").isJsonNull() && search.getAsJsonObject("aoo").has("id") && !search.getAsJsonObject("aoo").get("id").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.id.eq(Long.parseLong(search.getAsJsonObject("aoo").get("id").getAsString().trim())).or(QRubricaBeneficiario.rubricaBeneficiario.aoo.isNull()));
			}
			
			
			/*if(search.has("aoo") && !search.get("aoo").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
						((QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.codice.concat(" - ").concat(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.descrizione)).containsIgnoreCase(search.get("aoo").getAsString())).or(
								(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.codice.concat("-").concat(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.descrizione)).containsIgnoreCase(search.get("aoo").getAsString()))
										);
			}*/
			if(search.has("aooId") && !search.get("aooId").getAsString().trim().equalsIgnoreCase("") && StringUtils.isNumeric(search.get("aooId").getAsString())){
				predicate = predicate.and(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.isNull()
										.or(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.id.eq(search.get("aooId").getAsLong()))
										);
			}
			return rubricaDestinatarioEsternoRepository.findAll(predicate, paginazione);
		}
	}
	
	
	@Transactional(readOnly=true)
	public Page<RubricaDestinatarioEsterno> findByAooIdAndValidi(Long aooId, Pageable generatePageRequest) {
		log.debug("findByAooIdAndValidi aooId:"+aooId);
		BooleanExpression predicateRubrica = QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.id.eq( aooId).or(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.id.isNull());
		predicateRubrica = predicateRubrica.and(validitaService.createPredicate(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.validita));
		Page<RubricaDestinatarioEsterno> profili = rubricaDestinatarioEsternoRepository.findAll(predicateRubrica, generatePageRequest);
		for (RubricaDestinatarioEsterno rubricaDestinatarioEsterno : profili) {
			rubricaDestinatarioEsterno.setAoo( DomainUtil.minimalAoo(rubricaDestinatarioEsterno.getAoo()));
			DomainUtil.nameCerca( rubricaDestinatarioEsterno );
		}
		return profili;
	}
	
	@Transactional( readOnly=true )
	public Iterable<RubricaDestinatarioEsterno> findByAooIdAndValidi(Long aooId ) {
		log.debug("findByAooIdAndValidi aooId:"+aooId);
		BooleanExpression predicateRubrica = QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.aoo.id.eq( aooId);
		predicateRubrica = predicateRubrica.and(validitaService
				.createPredicate(QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.validita));
		Iterable<RubricaDestinatarioEsterno> profili = rubricaDestinatarioEsternoRepository.findAll(predicateRubrica);
		for (RubricaDestinatarioEsterno rubricaDestinatarioEsterno : profili) {
			rubricaDestinatarioEsterno.setAoo( DomainUtil.minimalAoo(rubricaDestinatarioEsterno.getAoo()));
			DomainUtil.nameCerca( rubricaDestinatarioEsterno );
		}
		return profili;
	}

	@Transactional( readOnly=true )
	public Page<RubricaDestinatarioEsterno> findAll(Pageable generatePageRequest) {
		Page<RubricaDestinatarioEsterno> l = rubricaDestinatarioEsternoRepository.findAll(generatePageRequest);
		for (RubricaDestinatarioEsterno rubricaDestinatarioEsterno : l) {
			rubricaDestinatarioEsterno.setAoo( DomainUtil.minimalAoo(rubricaDestinatarioEsterno.getAoo()));
			DomainUtil.nameCerca( rubricaDestinatarioEsterno );
		}
		return l;
	}
	
	@Transactional(readOnly=true)
	public Iterable<RubricaDestinatarioEsterno> findByIds(Set<Long> destinatariEsterniIds){
		BooleanExpression predicate = QRubricaDestinatarioEsterno.rubricaDestinatarioEsterno.id.in(destinatariEsterniIds);
		return rubricaDestinatarioEsternoRepository.findAll(predicate);
	}

	public RubricaDestinatarioEsterno save(RubricaDestinatarioEsterno destinatario) throws GestattiCatchedException {
		
		if(destinatario.getAoo()!=null && destinatario.getAoo().getId() == null) {
			destinatario.setAoo(null);
		}
		
		if(destinatario.getNotificaGiuntaAutomatica() == null){
			destinatario.setNotificaGiuntaAutomatica(false);
		}
		
		if(destinatario.getTipo().equalsIgnoreCase("privato")){
			destinatario.setDenominazione(null);
		}else if(destinatario.getTipo().equalsIgnoreCase("azienda")){
			destinatario.setNome(null);
			destinatario.setCognome(null);
			destinatario.setTitolo(null);
		}else{
			throw new GestattiCatchedException("Errore per mancanza di dati obbligatori.");
		}
		return rubricaDestinatarioEsternoRepository.save(destinatario);
	}
	
	public void delete(Long id) {
		RubricaDestinatarioEsterno entity = rubricaDestinatarioEsternoRepository.findOne(id);
		if( entity.getValidita() == null ){
			entity.setValidita(new Validita());
		}
		
		entity.getValidita().setValidoal(new LocalDate() );
		rubricaDestinatarioEsternoRepository.save( entity);
	}
	
	@Transactional(readOnly=false)
	public void disable(final Long id){
		log.debug("disable RubricaDestinatarioEsterno with id " + id);
		RubricaDestinatarioEsterno rubricaDestinatarioEsterno = rubricaDestinatarioEsternoRepository.findOne(id);
		if(rubricaDestinatarioEsterno!=null){
			if(rubricaDestinatarioEsterno.getValidita()==null){
				rubricaDestinatarioEsterno.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			rubricaDestinatarioEsterno.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			rubricaDestinatarioEsternoRepository.save(rubricaDestinatarioEsterno);
		}
	}
	
	@Transactional(readOnly=false)
	public void enable(final Long id){
		log.debug("enable RubricaDestinatarioEsterno with id " + id);
		RubricaDestinatarioEsterno rubricaDestinatarioEsterno = rubricaDestinatarioEsternoRepository.findOne(id);
		if(rubricaDestinatarioEsterno!=null){
			if(rubricaDestinatarioEsterno.getValidita()==null){
				rubricaDestinatarioEsterno.setValidita(new Validita());
			}
			rubricaDestinatarioEsterno.getValidita().setValidoal(null);
			rubricaDestinatarioEsternoRepository.save(rubricaDestinatarioEsterno);
		}
	}


	@Transactional(readOnly=true)
	public RubricaDestinatarioEsterno findOne(Long id) {
		return rubricaDestinatarioEsternoRepository.findOne(id);		 
	}
	
	
}

package it.linksmt.assatti.service;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Indirizzo;
import it.linksmt.assatti.datalayer.domain.QUfficio;
import it.linksmt.assatti.datalayer.domain.Ufficio;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.repository.UfficioRepository;
import it.linksmt.assatti.service.dto.UfficioSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class UfficioService {

	private final Logger log = LoggerFactory.getLogger(UfficioService.class);

	@Inject
	private UfficioRepository ufficioRepository;
	@Inject
	private ValiditaService validitaService;
	@Inject
	private IndirizzoService indirizzoService;
 
	@Transactional(readOnly=true)
	public List<Ufficio> findByAooId(Long aooId) {
		List<Ufficio> l = ufficioRepository.findByAooId(aooId);
		return l;
	}
	
	@Transactional(readOnly=true)
	public Page<Ufficio> searchUfficioByDto(UfficioSearchDTO search, Integer offset, Integer limit){
		BooleanExpression predicateUfficio = QUfficio.ufficio.id.isNotNull();
    	
    	Long idLong = null;
		if(search.getId()!=null && !"".equals(search.getId().trim())){
			try{
				idLong = Long.parseLong(search.getId().trim());
			}catch(Exception e){};
		}
		if(idLong!=null){
			predicateUfficio = predicateUfficio.and(QUfficio.ufficio.id.eq(idLong));
		}
		if(search.getCodice()!=null && !"".equals(search.getCodice().trim())){
			predicateUfficio = predicateUfficio.and(QUfficio.ufficio.codice.containsIgnoreCase(search.getCodice().trim()));
		}
		if(search.getDescrizione()!=null && !"".equals(search.getDescrizione().trim())){
			predicateUfficio = predicateUfficio.and(QUfficio.ufficio.descrizione.containsIgnoreCase(search.getDescrizione().trim()));
		}
		if(search.getEmail()!=null && !"".equals(search.getEmail().trim())){
			predicateUfficio = predicateUfficio.and(QUfficio.ufficio.email.containsIgnoreCase(search.getEmail().trim()));
		}
		if(search.getPec()!=null && !"".equals(search.getPec().trim())){
			predicateUfficio = predicateUfficio.and(QUfficio.ufficio.pec.containsIgnoreCase(search.getPec().trim()));
		}
		if(search.getResponsabile()!=null && !"".equals(search.getResponsabile().trim())){
			predicateUfficio = predicateUfficio.and(QUfficio.ufficio.responsabile.utente.cognome.concat(" ").concat(QUfficio.ufficio.responsabile.utente.nome).containsIgnoreCase(search.getResponsabile().trim()));
		}
		if(search.getAoo()!=null && !"".equals(search.getAoo().trim())){
			predicateUfficio = predicateUfficio.and(
					((QUfficio.ufficio.aoo.codice.concat(" - ").concat(QUfficio.ufficio.aoo.descrizione)).containsIgnoreCase(search.getAoo())).or(
									(QUfficio.ufficio.aoo.codice.concat("-").concat(QUfficio.ufficio.aoo.descrizione)).containsIgnoreCase(search.getAoo()))
					);
		}
		
		String stato = search.getStato();
		if(stato != null && !"".equals(stato)){

			if("0".equals(stato)){ // Profili attivi
				predicateUfficio = predicateUfficio.and(validitaService.createPredicate(QUfficio.ufficio.validita));
			}
			else if("1".equals(stato)){ // Profili disattivati
				predicateUfficio = predicateUfficio.and(validitaService.validAoo(QUfficio.ufficio.validita));
			}
			else{ // Tutti

			}

		}
    	
        Page<Ufficio> page = ufficioRepository.findAll(predicateUfficio, PaginationUtil.generatePageRequest(offset, limit));
        
        return page;
	}
	
	
	@Transactional(readOnly=true)
	public Iterable<Ufficio> findByAooIdAndValidi(Long aooId) {
		BooleanExpression predicateProfilo = QUfficio.ufficio.aoo.id.eq( aooId);
		predicateProfilo = predicateProfilo.and(validitaService
				.createPredicate(QUfficio.ufficio.validita));
		Iterable<Ufficio> profili = ufficioRepository.findAll(predicateProfilo);
		return profili;
	}
	

	@Transactional( readOnly=true )
	public Page<Ufficio> findAll(Pageable generatePageRequest) {
		Page<Ufficio> l = ufficioRepository.findAll(generatePageRequest);
		return l;
	}

	
	public Ufficio save(Ufficio ufficio) {
		if(ufficio.getIndirizzo()!=null && ufficio.getIndirizzo().getId()!=null){
			Indirizzo indirizzo = indirizzoService.getById(ufficio.getIndirizzo().getId());
			ufficio.setIndirizzo(indirizzo);
		}
		return ufficioRepository.save(ufficio);
	}
	
	public void delete(Long id) {
		Ufficio ufficio = ufficioRepository.findOne(id);
		if( ufficio.getValidita() == null ){
			ufficio.setValidita(new Validita());
		}
		
		ufficio.getValidita().setValidoal(new LocalDate() );
		ufficioRepository.save( ufficio);
	}

	@Transactional(readOnly=false)
	public void disableUfficio(final Long id){
		log.debug("disableUfficio idufficio" + id);
		Ufficio ufficio = ufficioRepository.findOne(id);
		if(ufficio!=null){
			if(ufficio.getValidita()==null){
				ufficio.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			ufficio.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			ufficioRepository.save(ufficio);
		}
	}

	@Transactional(readOnly=false)
	public void enableUfficio(final Long id){
		log.debug("enableUfficio idufficio" + id);
		Ufficio ufficio = ufficioRepository.findOne(id);
		if(ufficio!=null){
			if(ufficio.getValidita()==null){
				ufficio.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			ufficio.getValidita().setValidoal(null);
			ufficioRepository.save(ufficio);
		}
	}
	
	
}

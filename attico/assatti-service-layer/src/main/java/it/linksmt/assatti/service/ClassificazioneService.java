package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Classificazione;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneRiversamento;
import it.linksmt.assatti.datalayer.domain.QClassificazione;
import it.linksmt.assatti.datalayer.domain.RiversamentoInAttesa;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerie;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerieEnum;
import it.linksmt.assatti.datalayer.domain.dto.DocumentoPdfDto;
import it.linksmt.assatti.datalayer.repository.ClassificazioneRepository;
import it.linksmt.assatti.datalayer.repository.TipoDocumentoSerieRepository;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing classificazione.
 */
@Service
@Transactional
public class ClassificazioneService {

	private final Logger log = LoggerFactory.getLogger(ClassificazioneService.class);

	@Inject
	private ClassificazioneRepository classificazioneRepository;
	
	@Inject
	private TipoDocumentoSerieRepository tipoDocumentoSerieRepository;
	
	@Transactional(readOnly=false)
	public void disableClassificazione(final Long id){
		log.debug("disableClassificazione classificazione" + id);
		Classificazione classificazione = classificazioneRepository.findOne(id);
		if(classificazione!=null){
			Calendar cal = Calendar.getInstance();
			classificazione.setValidoAl(new LocalDate(cal.getTimeInMillis()));
			classificazioneRepository.save(classificazione);
		}
	}

	@Transactional(readOnly=false)
	public void enableClassificazione(final Long id){
		log.debug("enableClassificazione classificazione" + id);
		Classificazione classificazione = classificazioneRepository.findOne(id);
		if(classificazione!=null){
			classificazione.setValidoAl(null);
			classificazioneRepository.save(classificazione);
		}
	}

	public void save(Classificazione classificazione) {
		Calendar cal = Calendar.getInstance();
		classificazione.setValidoDal(new LocalDate(cal.getTimeInMillis()));
		classificazioneRepository.save(classificazione);		
	}

	public Classificazione findByDocumentoPdf(DocumentoPdfDto documento) {
		
		List<Classificazione> classificazione = classificazioneRepository.findByTipoDocumentoSerieAndAooAndValidoAlIsNull(documento.getTipoDocumentoSerie(), documento.getAooSerie());
		if(classificazione.isEmpty()) {
			classificazione = classificazioneRepository.findByTipoDocumentoSerieAndAooIsNullAndValidoAlIsNull(documento.getTipoDocumentoSerie());
		}
		if(classificazione.isEmpty()) {
			classificazione = classificazioneRepository.findByTipoDocumentoSerieIsNullAndAooIsNullAndValidoAlIsNull();
		}
		if(!classificazione.isEmpty()) {
			return classificazione.get(0);
		}else 
			return null;
	}

	public List<Classificazione> findAll(String idTitolario, String idVoceTitolario, Long tipoAtto, Long aoo, Long tipoDocumentoSerie) {
		BooleanExpression predicateClassificazione = QClassificazione.classificazione.id.isNotNull();
		
		if(idTitolario!=null){
			predicateClassificazione = predicateClassificazione.and(QClassificazione.classificazione.idTitolario.containsIgnoreCase(idTitolario));
		}
		if(idVoceTitolario!=null){
			predicateClassificazione = predicateClassificazione.and(QClassificazione.classificazione.voceTitolarioCodice.containsIgnoreCase(idVoceTitolario).or(QClassificazione.classificazione.voceTitolarioDescrizione.containsIgnoreCase(idVoceTitolario)));
		}
		if(aoo!=null){
			predicateClassificazione = predicateClassificazione.and(QClassificazione.classificazione.aoo.id.eq(aoo));
		}
		if(tipoDocumentoSerie!=null){
			predicateClassificazione = predicateClassificazione.and(QClassificazione.classificazione.tipoDocumentoSerie.id.eq(tipoDocumentoSerie));
		}
		Iterator<Classificazione> iterator = classificazioneRepository.findAll(predicateClassificazione).iterator();
		List<Classificazione> result = new ArrayList<Classificazione>();
	    while (iterator.hasNext()) {
	    	result.add(iterator.next());
	    }
	    
		return result;
	}

	public Classificazione findOne(Long id) {
		return classificazioneRepository.findOne(id);
	}

	public Classificazione findByTipoDocumentoSerie(TipoDocumentoSerieEnum tipodocumento) {
		TipoDocumentoSerie tipoDocumentoSerie = tipoDocumentoSerieRepository.findByCodice(tipodocumento.getValue());
		if(!classificazioneRepository.findByTipoDocumentoSerie(tipoDocumentoSerie).isEmpty()){
			return classificazioneRepository.findByTipoDocumentoSerie(tipoDocumentoSerie).get(0); 
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public Classificazione findByRiversamento(RiversamentoInAttesa riversamento) throws Exception{
		Classificazione c = null;
		BooleanExpression p = null;
		//Tutte le aoo OR Aoo specifica
		if(riversamento.getConfigurazioneRiversamento().getAooId().equals(0L) ||  riversamento.getConfigurazioneRiversamento().getAooId() > 0L){
			if(riversamento.getAooId()!=null && riversamento.getAooId() > 0L){
				p = QClassificazione.classificazione.aoo.id.eq(riversamento.getAooId()).and(QClassificazione.classificazione.tipoDocumentoSerie.id.eq(riversamento.getConfigurazioneRiversamento().getTipoDocumentoSerie().getId())).and(QClassificazione.classificazione.validoAl.isNull());
				if(classificazioneRepository.count(p) < 1L){
					p = QClassificazione.classificazione.aoo.id.isNull().and(QClassificazione.classificazione.tipoDocumentoSerie.id.eq(riversamento.getConfigurazioneRiversamento().getTipoDocumentoSerie().getId())).and(QClassificazione.classificazione.validoAl.isNull());
				}
			}else{
				throw new Exception("findByConfigurazioneAndRiversamento error - aoo riversamento non settata ma prevista da configurazione");
			}
		}else{
			//Nessuna aoo
			if(riversamento.getConfigurazioneRiversamento().getAooId().equals(-1L)){
				p = QClassificazione.classificazione.aoo.id.isNull();
				p = p.and(QClassificazione.classificazione.tipoDocumentoSerie.id.eq(riversamento.getConfigurazioneRiversamento().getTipoDocumentoSerie().getId()).and(QClassificazione.classificazione.validoAl.isNull()));
			}else{
				throw new Exception("Errore configurazione AOO");
			}
		}
		c = classificazioneRepository.findAll(p).iterator().next();
		return c;
	}
	
}

package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;


/**
 * Service class for cancelling attos at the end of year.
 */
@Service
@Transactional
public class AnnullamentoService {
	private final Logger log = LoggerFactory.getLogger(AnnullamentoService.class);
	
	@Inject
	private EventoService eventoService;
	
	// TODO: integrazione con Motore BPMN
	// @Inject
	// private BpmWrapperUtil bpmWrapperService;
	
	@Inject
	private AttoService attoService;

	@Transactional
	public void annullamentoAtti(List<Long> attoIdsDaAnnullare){
		String statoAnnullamento = WebApplicationProps.getProperty(ConfigPropNames.STATO_ATTO_ANNULLAMENTO_AUTOMATICO);
		
		for(Long attoId : attoIdsDaAnnullare){
			attoService.updateStato(statoAnnullamento, attoId);
			eventoService.annullamentoAutomatico(attoId);
		}
		boolean success = this.terminazioneProcessiCamunda(attoIdsDaAnnullare);
		log.debug("success: " + success);
	}
	
	private boolean terminazioneProcessiCamunda(List<Long> attoIdsDaAnnullare){
		boolean success = false;
		if(attoIdsDaAnnullare.size() > 0){
			log.info("Starting terminazione su camunda");
			// TODO: integrazione con motore BPMN
			String nProcessiTerminati = "0"; // bpmWrapperService.terminaProcessi(attoIdsDaAnnullare);
			Integer intProcessiTerminati = Integer.parseInt(nProcessiTerminati);
			log.info("Sono stati terminati correttamente " + nProcessiTerminati + " processi su camunda");
			if(intProcessiTerminati < attoIdsDaAnnullare.size()){
				log.warn("Qualcosa è andato storto, risultano meno processi terminati di quanti siano gli atti per cui è stato richiesto l'annullamento.");
			}else{
				success = true;
			}
		}else{
			log.info("Nessun processo da terminare");
		}
		return success;
	}
	
	
}

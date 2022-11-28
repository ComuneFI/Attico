package it.linksmt.assatti.bpm.delegate.impl;

import java.util.List;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.delegate.rollback.DelegateRollbackUtil;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.EventoEnum;
import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.service.EventoService;
import it.linksmt.assatti.service.JobPubblicazioneService;

/**
 * Service Task Camunda per la pubblicazione di un atto sull'albo pretorio.
 *
 */
@Service
@Transactional
public class PubblicazioneAlboServiceImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(PubblicazioneAlboServiceImpl.class);

	@Inject
	private BpmWrapperUtil bpmWrapperUtil;

	@Inject
	private JobPubblicazioneService jobPubblicazioneService;
	
	@Inject
	private EventoService eventoService;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		Boolean rollbackEnabled = rollbackInfo.get("enabled").getAsBoolean();
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());

		log.info("Verifica invio atto con id " + atto.getId() + " pubblicazione albo");

		// Verifica pubblicazione atto su albo
		String riservato = "true";
		if (atto.getRiservato() != null) {
			riservato = String.valueOf(atto.getRiservato()).toLowerCase();
		}
		
		execution.setVariable(AttoProcessVariables.RISERVATO, riservato);

//		if (riservato.equals("true")) {
//			log.info("Atto con id " + atto.getId() + " non pubblicabile :: riservato : " + riservato);
//		}
//		else {
		
			List<JobPubblicazione> checkJob = jobPubblicazioneService.findByAttoId(atto.getId().longValue());
			if (checkJob == null || checkJob.isEmpty()) {
				Long id = jobPubblicazioneService.richiediNuovaPubblicazione(atto.getId());
				if(!rollbackInfo.has("JobPubblicazioniAlbo")) {
					rollbackInfo.add("JobPubblicazioniAlbo", new JsonArray());
				}
				rollbackInfo.get("JobPubblicazioniAlbo").getAsJsonArray().add(new JsonPrimitive(id));
			}
			
			if(!rollbackInfo.has("eventiPubblicazioneAlbo")) {
				rollbackInfo.add("eventiPubblicazioneAlbo", new JsonArray());
			}
			Long eventoId = eventoService.saveEvento(EventoEnum.EVENTO_ATTESA_PUBBLICAZIONE_ALBO.getDescrizione(), atto).getId();
			if(!rollbackInfo.has("eventiPubblicazioneAlbo")) {
				rollbackInfo.add("eventiPubblicazioneAlbo", new JsonArray());
			}
			rollbackInfo.get("eventiPubblicazioneAlbo").getAsJsonArray().add(new JsonPrimitive(eventoId));
		
//		}
		
		if(rollbackEnabled) {
			execution.setVariable(AttoProcessVariables.ROLLBACK_INFO, rollbackInfo.toString());
		}
	}
}

package it.linksmt.assatti.bpm.delegate.impl;

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
import it.linksmt.assatti.service.EventoService;
import it.linksmt.assatti.service.JobTrasparenzaService;

/**
 * Service Task Camunda per la pubblicazione di un atto in trasparenza.
 *
 */
@Service
@Transactional
public class PubblicazioneTrasparenzaServiceImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(PubblicazioneTrasparenzaServiceImpl.class);

	@Inject
	private BpmWrapperUtil bpmWrapperUtil;

	@Inject
	private JobTrasparenzaService jobTrasparenzaService;

	@Inject
	private EventoService eventoService;

	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {

		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		Boolean rollbackEnabled = rollbackInfo.get("enabled").getAsBoolean();
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());

		log.info("Invio atto con id " + atto.getId() + " in pubblicazione trasparenza");

		Long id = jobTrasparenzaService.richiediNuovaPubblicazione(atto.getId());
		if(!rollbackInfo.has("JobPubblicazioniTrasparenza")) {
			rollbackInfo.add("JobPubblicazioniTrasparenza", new JsonArray());
		}
		rollbackInfo.get("JobPubblicazioniTrasparenza").getAsJsonArray().add(new JsonPrimitive(id));
		
		Long eventoId = eventoService.saveEvento(EventoEnum.EVENTO_ATTESA_PUBBLICAZIONE_TRASPARENZA.getDescrizione(), atto).getId();
		if(!rollbackInfo.has("eventiPubblicazioneTrasparenza")) {
			rollbackInfo.add("eventiPubblicazioneTrasparenza", new JsonArray());
		}
		rollbackInfo.get("eventiPubblicazioneTrasparenza").getAsJsonArray().add(new JsonPrimitive(eventoId));
		
		if(rollbackEnabled) {
			execution.setVariable(AttoProcessVariables.ROLLBACK_INFO, rollbackInfo.toString());
		}
	}
}

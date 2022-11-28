package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.delegate.rollback.DelegateRollbackUtil;
import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.service.EventoService;
import it.linksmt.assatti.utility.StringUtil;

@Service
@Transactional
public class RegistrazioneEventoServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(RegistrazioneEventoServiceImpl.class);
	
	@Inject
	private PlatformTransactionManager transactionManager;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private EventoService eventoService;
	

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		
		String nuovoStato = (String)execution.getVariableLocal(AttoProcessVariables.IMPOSTA_STATO);
		String codiceEvento = (String)execution.getVariableLocal(AttoProcessVariables.REGISTRA_EVENTO);
		
		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		Boolean rollbackEnabled = rollbackInfo.get("enabled").getAsBoolean();
		
		log.info("Registrazione Evento - Atto:  " + atto.getCodiceCifra());
		log.info("Stato:  " + nuovoStato);
		log.info("Evento: " + codiceEvento);
		log.info("Attività:  " + BpmThreadLocalUtil.getNomeAttivita());
		log.info("Profilo:   " + BpmThreadLocalUtil.getProfiloId());
		log.info("Delegante: " + BpmThreadLocalUtil.getProfiloDeleganteId());
		
		String nomeAttivita = BpmThreadLocalUtil.getNomeAttivita();
		if (execution.hasVariableLocal(AttoProcessVariables.AZIONE_UTENTE)) {
			nomeAttivita = (String)execution.getVariableLocal(AttoProcessVariables.AZIONE_UTENTE);
		}
		
		if (StringUtil.isNull(nuovoStato) && StringUtil.isNull(codiceEvento)) {
			nuovoStato = atto.getStato();
		}
		
		if (!StringUtil.isNull(nuovoStato)) {
			if(!rollbackInfo.has("avanzamenti")) {
				rollbackInfo.add("avanzamenti", new JsonArray());
			}
			Long avanzamentoId = registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), nuovoStato, nomeAttivita);
			rollbackInfo.get("avanzamenti").getAsJsonArray().add(new JsonPrimitive(avanzamentoId));
		}
		if (!StringUtil.isNull(codiceEvento)) {
			if(!rollbackInfo.has("eventi")) {
				rollbackInfo.add("eventi", new JsonArray());
			}
			Long eventoId = eventoService.saveEvento(codiceEvento, atto).getId();
			rollbackInfo.get("eventi").getAsJsonArray().add(new JsonPrimitive(eventoId));
		}
		
		if(rollbackEnabled) {
			execution.setVariable(AttoProcessVariables.ROLLBACK_INFO, rollbackInfo.toString());
		}
	}
}

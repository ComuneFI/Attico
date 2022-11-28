package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.EventoEnum;
import it.linksmt.assatti.service.EventoService;

@Service
@Transactional
public class SendBozzaMovimentiContabiliServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(SendBozzaMovimentiContabiliServiceImpl.class);
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private ContabilitaServiceWrapper contabilitaServiceWrapper;
	
	@Inject
	private EventoService eventoService;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			
			contabilitaServiceWrapper.sendBozza(atto, -1);
			
			eventoService.saveEvento(EventoEnum.JENTE_INVIO_DATI_PROPOSTA.getDescrizione(), atto);
		}
		catch(Exception se) {
			log.error("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati della proposta al sistema contabile.", se);
			
			throw new RuntimeException("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati della proposta al sistema contabile.", se);
		}
	}
}

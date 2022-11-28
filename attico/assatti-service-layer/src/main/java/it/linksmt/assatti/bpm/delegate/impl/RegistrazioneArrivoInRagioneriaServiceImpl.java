package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DatiContabili;
import it.linksmt.assatti.service.AttoService;

@Service
@Transactional
public class RegistrazioneArrivoInRagioneriaServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(RegistrazioneArrivoInRagioneriaServiceImpl.class);
	
	@Inject
	private AttoService attoService;
	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		log.info("RegistrazioneArrivoInRagioneriaService start atto " + execution.getProcessBusinessKey());
		
		Atto atto = attoService.findOneSimple(Long.parseLong(execution.getProcessBusinessKey()));
		if(atto.getDatiContabili()==null) {
			atto.setDatiContabili(new DatiContabili(false));
			atto.getDatiContabili().setId(atto.getId());
		}
		atto.getDatiContabili().setDataArrivoRagioneria(new DateTime());
		
		int numArrivi = 0;
		if (atto.getDatiContabili().getNumArriviRagioneria() != null) {
			numArrivi = Math.max(0, atto.getDatiContabili().getNumArriviRagioneria().intValue());
		}
		atto.getDatiContabili().setNumArriviRagioneria(numArrivi+1);
		
		attoService.save(atto);
		log.info("RegistrazioneArrivoInRagioneriaService end atto " + execution.getProcessBusinessKey());
	}
}

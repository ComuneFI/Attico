package it.linksmt.assatti.bpm.delegate.rollback;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.service.AttoService;

/**
 * Service Task Camunda per l'aggiornamento della data esecutivita di un atto.
 *
 * @author Gianluca Pindinelli
 *
 */
@Service
public class DataEsecutivitaDopoPubblicazioneRollbackService implements JavaDelegate {

	private final Logger log = LoggerFactory.getLogger(DataEsecutivitaDopoPubblicazioneRollbackService.class);

	@Inject
	private BpmWrapperUtil bpmWrapperUtil;

	@Inject
	private AttoService attoService;

	@Override
	@Transactional
	public void execute(DelegateExecution execution) throws Exception {
		
		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		if(rollbackInfo.has("dataEsecutivitaDopoPubblicazione")) {
			
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			atto.setDataEsecutivita(null);
			attoService.save(atto);
			
			log.info("Data esecutività set to null");
		}else {
			log.info("nothing to rollback");
		}
	}
}

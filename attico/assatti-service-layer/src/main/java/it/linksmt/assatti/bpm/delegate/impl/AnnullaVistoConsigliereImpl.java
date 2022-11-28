package it.linksmt.assatti.bpm.delegate.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.Execution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.util.MessageKeys;

@Service
@Transactional
public class AnnullaVistoConsigliereImpl implements DelegateBusinessLogic {
	
	private final static Logger LOGGER = Logger.getLogger(AnnullaVistoConsigliereImpl.class.getName());

	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		// Segnalazione per terminare il visto consigliere, verifica se esistono task da terminare
		List<Execution> assList = runtimeService.createExecutionQuery()
				.messageEventSubscriptionName(MessageKeys.ANNULLA_VISTO_CONSIGLIERE)
				.processInstanceBusinessKey(execution.getProcessBusinessKey())
				.active().list();
		
		if (assList != null && !assList.isEmpty()) {
			runtimeService.correlateMessage(
					MessageKeys.ANNULLA_VISTO_CONSIGLIERE, execution.getProcessBusinessKey());
		}
		else  {
			// Se il consigliere ha già dato il visto
			LOGGER.log(Level.WARNING, "Impossibile inviare il messaggio.");
		}
	}	
}

package it.linksmt.assatti.bpm.delegate.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.util.MessageKeys;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;

@Service
@Transactional
public class AnnullaVistoAssessoreImpl implements DelegateBusinessLogic {
	
	private final static Logger LOGGER = Logger.getLogger(AnnullaVistoAssessoreImpl.class.getName());
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private TaskService taskService;

	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		
		List<Task> list = taskService.createTaskQuery()
				.processInstanceBusinessKey(
						String.valueOf(atto.getId().longValue()))
				.active().list();
		
		// Segnalazione per terminare il visto assessore, verifica se esistono task da terminare
		List<Execution> assList = runtimeService.createExecutionQuery()
				.messageEventSubscriptionName(MessageKeys.ANNULLA_VISTO_ASSESSORE)
				.processInstanceBusinessKey(execution.getProcessBusinessKey())
				.active().list();
		
		if (assList != null && !assList.isEmpty() && list!=null && !list.isEmpty()) {
			//runtimeService.correlateMessage(
				//	MessageKeys.ANNULLA_VISTO_ASSESSORE, execution.getProcessBusinessKey());
			
			for (Execution execution2 : assList) {
				Map<String, Object> correlationKeys = new HashMap<String, Object>();
				correlationKeys.put("ID_ATTO",execution.getProcessBusinessKey());
				
				for (Task task : list) {
					if(task.getExecutionId().equalsIgnoreCase(execution2.getId())) {
						correlationKeys.put("ASSESSORE_VISTO",task.getAssignee());
						runtimeService.correlateMessage(MessageKeys.ANNULLA_VISTO_ASSESSORE, correlationKeys );
						break;
					}
				}
				
			}
		}
		else  {
			// Se l'assessore ha già dato il visto
			LOGGER.log(Level.WARNING, "Impossibile inviare il messaggio.");
		}
	}	
}

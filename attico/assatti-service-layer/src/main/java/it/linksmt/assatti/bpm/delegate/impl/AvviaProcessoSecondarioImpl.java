package it.linksmt.assatti.bpm.delegate.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.utility.StringUtil;

@Service
@Transactional
public class AvviaProcessoSecondarioImpl implements DelegateBusinessLogic {
	
	private final static Logger LOGGER = Logger.getLogger(AvviaProcessoSecondarioImpl.class.getName());
	
	@Inject
	private WorkflowServiceWrapper workflowServiceWrapper;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		
		String processDefinitionKey = (String)execution.getVariableLocal(AttoProcessVariables.START_PROCESS_DEFINITION_KEY);
		String startProcessCollection = (String)execution.getVariableLocal(AttoProcessVariables.START_PROCESS_COLLECTION);
		String startProcessElementVariable = (String)execution.getVariableLocal(AttoProcessVariables.START_PROCESS_ELEMENT_VARIABLE);
		
		if (StringUtil.isNull(processDefinitionKey)) {
			throw new RuntimeException("L'identificativo del processo da avviare risulta nullo.");
		}
		
		if (StringUtil.isNull(startProcessElementVariable)) {
			return;
		}
		
		Object startProcessVal = execution.getVariable(startProcessCollection);
		if (startProcessVal == null) {
			workflowServiceWrapper.avviaProcessoBpmSecondario(atto, processDefinitionKey, null);
		}
		else if (startProcessVal instanceof Collection) {
			Collection startProcessColl = (Collection) startProcessVal;
			for (Object objectVal : startProcessColl) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put(startProcessElementVariable, objectVal);
				
				LOGGER.info("AVVIO PROCESSO: " + processDefinitionKey + " - VAR: " + startProcessElementVariable + "=" + objectVal);
				workflowServiceWrapper.avviaProcessoBpmSecondario(atto, processDefinitionKey, variables);
			}
		}
		else {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(startProcessElementVariable, startProcessVal);
			
			LOGGER.info("AVVIO PROCESSO: " + processDefinitionKey + " - VAR: " + startProcessElementVariable + "=" + startProcessVal);
			workflowServiceWrapper.avviaProcessoBpmSecondario(atto, processDefinitionKey, variables);
		}
	}	
}

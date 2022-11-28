package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.datalayer.domain.StatoConclusoEnum;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DocumentoPdfService;

@Service
@Transactional
public class RegistrazioneFineIterServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(RegistrazioneFineIterServiceImpl.class);
	
	@Inject
	private AttoService attoService;
	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		log.info("RegistrazioneFineIter start atto " + execution.getProcessBusinessKey());
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(execution.getProcessInstanceId()).singleResult();
		if(executionEntity!=null && !executionEntity.isConcurrent()) {
			String tipo = (String)execution.getVariableLocal(AttoProcessVariables.TIPO_FINE_ITER);
			
			StatoConclusoEnum tipoEnum = null;
			if(tipo!=null) {
				for(int i = 0; i < StatoConclusoEnum.values().length; i++) {
					if(StatoConclusoEnum.values()[i].getCodice().equals(tipo)) {
						tipoEnum = StatoConclusoEnum.values()[i];
						break;
					}
				}
			}
			
			if(tipoEnum!=null) {
				attoService.impostaFineIter(Long.parseLong(execution.getProcessBusinessKey()), tipoEnum);
			}else {
				throw new RuntimeException("Tipo Fine Iter NULL o non valido: " + tipo);
			}
			
			log.info("RegistrazioneFineIter end for atto " + execution.getProcessBusinessKey() + " with type " + tipoEnum.getCodice());
		}else if(executionEntity==null) {
			throw new RuntimeException("ExecutionEntity is NULL for executionid " + execution.getProcessInstanceId());
		}else {
			log.info("RegistrazioneFineIter non da eseguire per atto " + execution.getProcessBusinessKey());
		}
	}
}

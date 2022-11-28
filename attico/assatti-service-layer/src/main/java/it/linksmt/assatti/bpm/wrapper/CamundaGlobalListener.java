package it.linksmt.assatti.bpm.wrapper;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import javax.transaction.Transactional;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.service.TaskRiassegnazioneService;
import it.linksmt.assatti.utility.Constants;

@Component
public class CamundaGlobalListener implements TaskListener, ExecutionListener, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(CamundaGlobalListener.class.getName());
  
  public void notify(DelegateExecution execution) throws Exception {
	  ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	  RuntimeService runtimeService = processEngine.getRuntimeService();
	  ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(execution.getProcessInstanceId()).singleResult();
		if(executionEntity!=null && !executionEntity.isConcurrent() && execution.getEventName().equalsIgnoreCase(ExecutionListener.EVENTNAME_END)) {
		  if(executionEntity.isEnded()) {
			  log.info("Process " + execution.getBusinessKey() + " end");
		  }
	  }
  }

  @Transactional
  public void notify(DelegateTask task){
	  if(task.getEventName().equalsIgnoreCase("assignment")) {
		  if(task.getAssignee()!=null && !task.getAssignee().trim().isEmpty()) {
			  TaskRiassegnazioneService taskRiassegnazioneService = SpringApplicationContextHolder.getApplicationContext().getBean(TaskRiassegnazioneService.class);
			  String reassignmentValue = taskRiassegnazioneService.getReassignmentValue(task.getId(), task.getAssignee(), Long.parseLong(task.getExecution().getProcessInstance().getBusinessKey()));
			  if(reassignmentValue != null && !reassignmentValue.trim().isEmpty() && !reassignmentValue.equals(task.getAssignee())){
				  task.setAssignee(reassignmentValue);
			  }else{
				  this.verificaDelegaTaskFullIter(task);
			  }
		  }
	  }
  }
  
//task.getBpmnModelInstance().getDocumentElement().getAttributeValue("candidateGroups")  
  private void verificaDelegaTaskFullIter(DelegateTask task) {
		if(task.hasVariable(AttoProcessVariables.DELEGA_TASK_FULL_ITER)) {
			@SuppressWarnings("unchecked")
			Map<String, String> mapDeleganteDelegato = ((Map<String, String>)task.getVariable(AttoProcessVariables.DELEGA_TASK_FULL_ITER));
			for(String deleganteInfo : mapDeleganteDelegato.keySet()) {
				String delegante = deleganteInfo.split(Constants.BPM_INCARICO_SEPARATOR)[0];
				String delegato = mapDeleganteDelegato.get(deleganteInfo).split(Constants.BPM_INCARICO_SEPARATOR)[0];
				if(task.getAssignee().startsWith(delegante)) {
					task.setVariableLocal(AttoProcessVariables.PROFILO_DELEGANTE, task.getAssignee());
					task.setVariableLocal(AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE, true);
					task.setAssignee(task.getAssignee().replaceAll(delegante, delegato));
					log.info("Task " + task.getId() + " riassegnato per delega a " + task.getAssignee());
					break;
				}
			}
		}
  }

}
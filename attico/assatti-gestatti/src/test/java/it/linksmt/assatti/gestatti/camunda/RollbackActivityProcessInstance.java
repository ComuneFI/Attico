package it.linksmt.assatti.gestatti.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.linksmt.assatti.ServiceConfiguration;
import it.linksmt.assatti.datalayer.repository.GruppoRuoloRepository;
import it.linksmt.assatti.datalayer.repository.UtenteRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfiguration.class)
@IntegrationTest
public class RollbackActivityProcessInstance {

	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private UtenteRepository utenteRepository;

	/**
	 * https://docs.camunda.org/manual/7.8/user-guide/process-engine/process-instance-modification/
	 */
	
//	@Test()
	public void modificaProcesso() {
//		System.out.println("test utente");
//		System.out.println(utenteRepository.count());
//		System.out.println("end of test utente");
		System.out.println("START MODIFICA PROCESSO...\n\n\n");
		String processInstanceId = "d7634fef-a123-11ec-b4ab-005056a71a08";
		String taskIdDestinazione = "Task_0vdnfgl"; //questa instanza di task verrà creata
		String taskIdAttuale = "UserTask_08xya3o"; //questa instanza di task verrà cancellata
		String variableName = ""; //"GROUP_PARERE_ISTRUTTORIO_RESPONSABILE";
		String variableValue = ""; //"test";
		
		if(!variableName.isEmpty()) {
			System.out.println("variableName::"+variableName + "::variableValue::" + variableValue);
			runtimeService.createProcessInstanceModification(processInstanceId)
			  .startBeforeActivity(taskIdDestinazione)
			  .setVariable(variableName, variableValue)
			  .cancelAllForActivity(taskIdAttuale)
			  .execute();
		}else {
			runtimeService.createProcessInstanceModification(processInstanceId)
			  .startBeforeActivity(taskIdDestinazione)
			  .cancelAllForActivity(taskIdAttuale)
			  .execute();
		}
		System.out.println("PROCESSO MODIFICATO CON SUCCESSO!!!");
	}
}

package it.linksmt.assatti.bpm.delegate.rollback;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.util.AttoProcessVariables;

@Service
public class CleanRollbackInfoService implements JavaDelegate {
	
	private final Logger log = LoggerFactory.getLogger(CleanRollbackInfoService.class);
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String rollbackInfo = (String)execution.getVariable(AttoProcessVariables.ROLLBACK_INFO);
		if(rollbackInfo!=null) {
			execution.removeVariable(AttoProcessVariables.ROLLBACK_INFO);
			log.info(AttoProcessVariables.ROLLBACK_INFO + " cleaned");
		}
		
	}
}

package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.AnnullaEsitoServiceImpl;

@Service
public class AnnullaEsitoService implements JavaDelegate {
	
	@Inject
	private AnnullaEsitoServiceImpl service; 
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}


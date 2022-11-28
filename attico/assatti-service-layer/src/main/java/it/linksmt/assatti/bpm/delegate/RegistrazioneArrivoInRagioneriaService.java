package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.RegistrazioneArrivoInRagioneriaServiceImpl;

@Service
public class RegistrazioneArrivoInRagioneriaService implements JavaDelegate {
	
	@Inject
	private RegistrazioneArrivoInRagioneriaServiceImpl service;
	
	@Override
	public void execute(DelegateExecution execution) {
		service.executeBusinessLogic(execution);
	}
}

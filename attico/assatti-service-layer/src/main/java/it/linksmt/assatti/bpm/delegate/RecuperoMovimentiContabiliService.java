package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.RecuperoMovimentiContabiliServiceImpl;

@Service
public class RecuperoMovimentiContabiliService implements JavaDelegate {
	
	@Inject
	private RecuperoMovimentiContabiliServiceImpl service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}

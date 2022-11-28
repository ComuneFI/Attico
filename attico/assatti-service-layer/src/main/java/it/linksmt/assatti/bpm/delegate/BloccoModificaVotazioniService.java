package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.BloccoModificaVotazioniServiceImpl;

@Service
public class BloccoModificaVotazioniService implements JavaDelegate {
	
	@Inject
	private BloccoModificaVotazioniServiceImpl service;
	
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}

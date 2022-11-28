package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.PubblicazioneTrasparenzaServiceImpl;

/**
 * Service Task Camunda per la pubblicazione di un atto in trasparenza.
 *
 * @author Gianluca Pindinelli
 *
 */
@Service
public class PubblicazioneTrasparenzaService implements JavaDelegate {

	@Inject
	private PubblicazioneTrasparenzaServiceImpl service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}

package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.DataEsecutivitaDopoFirmaServiceImpl;

/**
 * Service Task Camunda per l'aggiornamento della data esecutivita di un atto.
 *
 * @author Gianluca Pindinelli
 *
 */
@Service
public class DataEsecutivitaDopoFirmaService implements JavaDelegate {

	@Inject
	private DataEsecutivitaDopoFirmaServiceImpl service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}

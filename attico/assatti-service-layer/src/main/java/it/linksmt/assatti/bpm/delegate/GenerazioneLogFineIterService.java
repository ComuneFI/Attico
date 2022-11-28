package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.GenerazioneLogFineIterServiceImpl;

/**
 * Service Task Camunda per la generazione del documento pdf di log fine iter
 *
 *
 */
@Service
public class GenerazioneLogFineIterService implements JavaDelegate {

	@Inject
	private GenerazioneLogFineIterServiceImpl service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}

package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.GenerazioneDocumentoCompletoServiceImpl;

/**
 * Service Task Camunda per la generazione del documento pdf completo
 *
 *
 */
@Service
public class GenerazioneDocumentoCompletoService implements JavaDelegate {

	@Inject
	private GenerazioneDocumentoCompletoServiceImpl service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}

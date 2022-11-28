package it.linksmt.assatti.bpm.delegate;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.delegate.impl.GenerazionePropostaLicenziataGiuntaServiceImpl;


@Service
public class GenerazionePropostaLicenziataGiuntaService implements JavaDelegate {

	@Inject
	private GenerazionePropostaLicenziataGiuntaServiceImpl service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.executeBusinessLogic(execution);
	}
}

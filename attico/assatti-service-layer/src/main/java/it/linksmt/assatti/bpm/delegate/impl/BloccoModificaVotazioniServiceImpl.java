package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.service.AttiOdgService;

@Service
@Transactional
public class BloccoModificaVotazioniServiceImpl implements DelegateBusinessLogic {
	
	@Inject
	private AttiOdgService attiOdgService;
	
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		attiOdgService.bloccoModificaVotazioni(Long.parseLong(execution.getProcessBusinessKey()));
	}
}

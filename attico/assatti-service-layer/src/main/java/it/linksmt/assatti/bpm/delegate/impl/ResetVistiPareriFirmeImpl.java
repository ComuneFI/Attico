package it.linksmt.assatti.bpm.delegate.impl;

import java.util.Set;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.datalayer.repository.ParereRepository;
import it.linksmt.assatti.datalayer.repository.SottoscrittoreAttoRepository;

@Service
@Transactional
public class ResetVistiPareriFirmeImpl implements DelegateBusinessLogic {
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private SottoscrittoreAttoRepository sottoscrittoreAttoRepository;
	
	@Inject
	private ParereRepository parereRepository;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		
		Set<Parere> pareri = atto.getPareri();
		if (pareri != null) {
			for (Parere parere : pareri) {
				parere.setAnnullato(true);
				parereRepository.save(parere);
			}
		}
		
		
		Set<SottoscrittoreAtto> sottoscrittori = atto.getSottoscrittori();
		if (sottoscrittori != null) {
			for (SottoscrittoreAtto sa : sottoscrittori) {
				sa.setEnabled(false);
				sottoscrittoreAttoRepository.save(sa);
			}
		}
	}	
}

package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.service.DocumentoPdfService;

/**
 * Service Task Camunda per la generazione del documento pdf di log fine iter
 *
 */
@Service
@Transactional
public class GenerazioneLogFineIterServiceImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(GenerazioneLogFineIterServiceImpl.class);

	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			try {
				documentoPdfService.generaReportIter(Long.parseLong(execution.getProcessBusinessKey()), true);
			}catch(Exception e) {
				if(e instanceof RuntimeException) {
					throw (RuntimeException)e;
				}else {
					throw new RuntimeException(e);
				}
			}
		}catch(Exception e) {
			if(e!=null && e instanceof RuntimeException) {
				throw (RuntimeException)e;
			}else {
				throw new RuntimeException(e);
			}
		}
	}
}

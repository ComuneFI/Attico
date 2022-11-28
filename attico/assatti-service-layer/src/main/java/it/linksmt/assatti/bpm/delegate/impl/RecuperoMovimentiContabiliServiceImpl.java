package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DatiContabili;
import it.linksmt.assatti.datalayer.repository.DatiContabiliRepository;

@Service
@Transactional
public class RecuperoMovimentiContabiliServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(RecuperoMovimentiContabiliServiceImpl.class);
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private ContabilitaServiceWrapper contabilitaServiceWrapper;
	
	@Inject
	private DatiContabiliRepository datiContabiliRepository;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			//2022-03-31: effettuo l'update dei movimenti contabile da jente solo se è ceccatto il flag includi movimento nell'atto.
			//questo fix è stato necessario perchè in produzione ci sono atti con più di un migliaio di movimenti su jente e quindi la chiamata va in timeout. 
			if(atto!=null) {
				DatiContabili datiCont = datiContabiliRepository.findOne(atto.getId());
				if(datiCont!=null && datiCont.getIncludiMovimentiAtto()!=null && datiCont.getIncludiMovimentiAtto().booleanValue()==true) {
					contabilitaServiceWrapper.updateMovimentiContabili(atto, 0, false, false);
				}
			}
		}
		catch(Exception se) {
			log.error("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati al sistema contabile.", se);
			
			throw new RuntimeException("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati al sistema contabile.", se);
		}
	}
}

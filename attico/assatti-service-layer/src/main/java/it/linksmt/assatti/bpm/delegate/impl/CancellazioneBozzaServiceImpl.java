package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.datalayer.domain.Atto;

@Service
@Transactional
public class CancellazioneBozzaServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(CancellazioneBozzaServiceImpl.class);
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private ContabilitaService contabilitaService;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		try {
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			
			String codiceTipoAtto = atto.getTipoAtto().getCodice();
			String numeroProposta = atto.getCodiceCifra().substring(atto.getCodiceCifra().length() - 5);
			int annoCreazioneProposta = atto.getDataCreazione().getYear();
			
			contabilitaService.eliminaBozza(codiceTipoAtto,
					numeroProposta, annoCreazioneProposta);
		}
		catch(Exception se) {
			log.error("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati al sistema contabile.", se);
			
			throw new RuntimeException("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati al sistema contabile.", se);
		}
	}

}

package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.datalayer.domain.Atto;

@Service
@Transactional
public class DataEsecutivitaJenteServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(DataEsecutivitaJenteServiceImpl.class);
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private ContabilitaService contabilitaService;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			
			if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
				if(atto.getIe()==null || !atto.getIe().booleanValue()) {
					if(contabilitaService.esisteBozzaAtto(atto.getTipoAtto().getCodice(),  atto.getNumeroAdozione(),  atto.getDataAdozione().getYear(), false)) {
						contabilitaService.dataEsecutivitaAtto(
								atto.getTipoAtto().getCodice(), 
								atto.getNumeroAdozione(), 
								atto.getDataAdozione().getYear(), 
								atto.getDataEsecutivita().toDate());
					}
				}
			}else {
			
				if(contabilitaService.esisteBozzaAtto(atto.getTipoAtto().getCodice(),  atto.getNumeroAdozione(),  atto.getDataAdozione().getYear(), false)) {
					contabilitaService.dataEsecutivitaAtto(
							atto.getTipoAtto().getCodice(), 
							atto.getNumeroAdozione(), 
							atto.getDataAdozione().getYear(), 
							atto.getDataEsecutivita().toDate());
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

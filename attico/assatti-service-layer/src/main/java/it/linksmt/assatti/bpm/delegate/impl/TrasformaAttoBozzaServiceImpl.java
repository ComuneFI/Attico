package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.cooperation.dto.contabilita.ContabilitaDto;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.service.AttoService;

@Service
@Transactional
public class TrasformaAttoBozzaServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(TrasformaAttoBozzaServiceImpl.class);
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private ContabilitaService contabilitaService;
	
	@Inject
	private AttoService attoService;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		try {
			Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
			if(atto.getNumeroAdozione() != null && atto.getDataAdozione() != null) {
				if(atto.getDatiContabili()!=null) {
					atto.getDatiContabili().setTrasformazioneWarning(true);
				}
				
				atto = attoService.save(atto);
				
				ContabilitaDto contabilitaDto = new ContabilitaDto();
				contabilitaDto.setCodiceTipoAtto(atto.getTipoAtto().getCodice());
				
				contabilitaDto.setAnnoAtto(atto.getDataNumerazione().getYear());
				contabilitaDto.setNumeroAtto(atto.getNumeroAdozione());
				
				if(contabilitaService.esisteBozzaAtto(atto.getTipoAtto().getCodice(),  atto.getNumeroAdozione(),  atto.getDataAdozione().getYear(), false)) {
					contabilitaService.revertBozza(contabilitaDto);
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

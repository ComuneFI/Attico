package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.NomiAttivitaAtto;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;

@Service
@Transactional
public class AnnullaEsitoServiceImpl implements DelegateBusinessLogic {
	
	@Inject
	private AttoRepository attoRepository; 
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private AttiOdgRepository attoOdgRepository;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;

	private final Logger log = LoggerFactory.getLogger(AnnullaEsitoServiceImpl.class);
	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		atto.setEsito("");
				
		Atto temp = attoRepository.save(atto);
		log.debug("Esito atto:" + temp.getEsito());
		
		Iterable<AttiOdg> l = attoOdgRepository.findByAtto(atto);
		AttiOdg attoOdgDaCancellare = null;
		for (AttiOdg attoOdg : l) {
			if(attoOdgDaCancellare==null || attoOdgDaCancellare.getId()<attoOdg.getId()){
				attoOdgDaCancellare = attoOdg;
			}
		}
		if(attoOdgDaCancellare!=null){
			attoOdgDaCancellare.setEsito("");
			attoOdgRepository.save(attoOdgDaCancellare);
		}
		
		registrazioneAvanzamentoService.impostaStatoAtto(atto.getId().longValue(),
				SedutaGiuntaConstants.statiAtto.propostaInAttesaDiEsito.toString(), 
				NomiAttivitaAtto.REGISTRA_ESITO);
	}
}


package it.linksmt.assatti.bpm.delegate.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.service.CodiceProgressivoService;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.utility.StringUtil;

@Service
@Transactional
public class NumerazionePropostaServiceImpl implements DelegateBusinessLogic {
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private CodiceProgressivoService codiceProgressivoService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		
		if (StringUtil.isNull(atto.getCodiceCifra())) {
			
			String codiceCifra = "";
			if(atto.getTipoAtto().getProgressivoPropostaAoo()) {
				codiceCifra = codiceProgressivoService.generaCodiceCifraProposta( atto.getAoo(), atto.getDataCreazione().getYear() , atto.getTipoAtto(), atto.getTipoAtto().getTipoProgressivo() );
			} else {
				codiceCifra = codiceProgressivoService.generaCodiceCifraProposta( null, atto.getDataCreazione().getYear() , atto.getTipoAtto(),  atto.getTipoAtto().getTipoProgressivo() );
			}
			
			atto.setCodiceCifra(codiceCifra);
			attoRepository.save(atto);
		}
	}
}

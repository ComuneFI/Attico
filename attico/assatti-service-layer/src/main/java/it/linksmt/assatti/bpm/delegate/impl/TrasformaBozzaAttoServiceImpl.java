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
public class TrasformaBozzaAttoServiceImpl implements DelegateBusinessLogic {
	
	private final Logger log = LoggerFactory.getLogger(TrasformaBozzaAttoServiceImpl.class);
	
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
			
			if(atto.getDatiContabili()!=null) {
				atto.getDatiContabili().setTrasformazioneWarning(false);
			}
			
			atto = attoService.save(atto);
			
			ContabilitaDto contabilitaDto = new ContabilitaDto();
			contabilitaDto.setCodiceTipoAtto(atto.getTipoAtto().getCodice());
			
			contabilitaDto.setAnnoCreazioneProposta(atto.getDataCreazione().getYear());
			contabilitaDto.setDataCreazioneProposta(atto.getDataCreazione().toDate());
			contabilitaDto.setNumeroProposta(atto.getCodiceCifra().substring(atto.getCodiceCifra().length() - 5));

			contabilitaDto.setAnnoAtto(atto.getDataNumerazione().getYear());
			contabilitaDto.setDataAdozioneAtto(atto.getDataAdozione().toDate());
			contabilitaDto.setNumeroAtto(atto.getNumeroAdozione());
			
			contabilitaDto.setOggetto(atto.getContabileOggetto());
			/*
			if (atto.getContabileImporto() != null) {
				contabilitaDto.setImportoTotale(new BigDecimal(atto.getContabileImporto().doubleValue()));
			}
			*/
			if(atto.getDataEsecutivita() != null) {
				contabilitaDto.setDataEsecutivita(atto.getDataEsecutivita().toDate());
			}
			contabilitaDto.setResponsabileProcedimento(null);
			
			contabilitaService.confirmAtto(contabilitaDto);
		}
		catch(Exception se) {
			log.error("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati al sistema contabile.", se);
			
			throw new RuntimeException("Il sistema ha riscontrato un errore "
					+ "durante l'invio dei dati al sistema contabile.", se);
		}
	}
}

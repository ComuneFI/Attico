package it.linksmt.assatti.bpm.delegate.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.service.CodiceProgressivoService;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.cooperation.dto.contabilita.ContabilitaDto;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@Transactional
public class NumerazioneAttoServiceImpl  implements DelegateBusinessLogic {
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private AttoService attoService;
	
	@Inject 
	private AttiOdgRepository attiOdgRepository;
	
	@Inject
	private CodiceProgressivoService codiceProgressivoService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private ContabilitaService contabilitaService;
	
	private final Logger log = LoggerFactory.getLogger(NumerazioneAttoServiceImpl.class);

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
	
		if (StringUtil.isNull(atto.getNumeroAdozione())) {


			boolean aggiornaDataAdozione = WebApplicationProps.getPropertyList(ConfigPropNames.LIST_TIPO_ATTO_AGGIORNA_DATA_ADOZIONE, new ArrayList<Object>()).contains(atto.getTipoAtto().getCodice());
			
			LocalDate dataAdozione;
			if(aggiornaDataAdozione) {
				dataAdozione = LocalDate.now();
			}else {
				//workaroud DL - data adozione non si aggiorna se esiste la data di esecutivit�
				if(atto.getTipoAtto().getCodice().equalsIgnoreCase("DL") && atto.getDataEsecutivita() == null){
					dataAdozione = LocalDate.now();
				}else {
					dataAdozione = atto.getDataAdozione()!= null ? atto.getDataAdozione():LocalDate.now();
				}
			}
			
			
			String numeroAdozione = null;
			
			if(atto.getTipoAtto().getProgressivoAdozioneAoo()) {
				numeroAdozione = codiceProgressivoService.generaCodiceCifraAdozione( atto.getAoo(), dataAdozione.getYear() ,  atto.getTipoAtto().getTipoProgressivo() );
			} else {
				numeroAdozione = codiceProgressivoService.generaCodiceCifraAdozione( null, dataAdozione.getYear() ,  atto.getTipoAtto().getTipoProgressivo() );
			}
			
			
			
			List<AttiOdg> ordineGiornoSet = attiOdgRepository.findByAtto(atto);
			AttiOdg esitoSel = null;
			for (AttiOdg attoOdg : ordineGiornoSet) {
				if (attoOdg.getEsito() != null && atto.getEsito() != null
						&& attoOdg.getEsito().equals(atto.getEsito())) {
					
					// Prendo l'ultimo odg in cui l'atto è stato inserito
					if (esitoSel == null || esitoSel.getId().longValue() < attoOdg.getId().longValue()) {
						esitoSel = attoOdg;
					}
				}
			}
			if(esitoSel != null && esitoSel.getDataDiscussione()!=null) {
				dataAdozione = esitoSel.getDataDiscussione();
			}else if(esitoSel != null && esitoSel.getOrdineGiorno()!=null && esitoSel.getOrdineGiorno().getSedutaGiunta()!=null) {
				if(esitoSel.getOrdineGiorno().getSedutaGiunta().getInizioLavoriEffettiva()!=null) {
					dataAdozione = esitoSel.getOrdineGiorno().getSedutaGiunta().getInizioLavoriEffettiva().toLocalDate();
				}
				else if(esitoSel.getOrdineGiorno().getSedutaGiunta().getSecondaConvocazioneInizio()!=null) {
					dataAdozione = esitoSel.getOrdineGiorno().getSedutaGiunta().getSecondaConvocazioneInizio().toLocalDate();
				}else if(esitoSel.getOrdineGiorno().getSedutaGiunta().getPrimaConvocazioneFine()!=null) {
					dataAdozione = esitoSel.getOrdineGiorno().getSedutaGiunta().getPrimaConvocazioneFine().toLocalDate();
				}
				else if(esitoSel.getOrdineGiorno().getSedutaGiunta().getPrimaConvocazioneInizio()!=null) {
					dataAdozione = esitoSel.getOrdineGiorno().getSedutaGiunta().getPrimaConvocazioneInizio().toLocalDate();
				}
			}
				
			atto.setNumeroAdozione(  numeroAdozione);
			atto.setDataNumerazione(LocalDate.now());
			atto.setDataAdozione( dataAdozione );
			
			
			if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
				if(atto.getDatiContabili()!=null) {
					atto.getDatiContabili().setTrasformazioneWarning(false);
				}
				
				if(atto.getIe()!=null && atto.getIe().booleanValue()) {
					
					if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null) {
						if(atto.getCodicecifraAttoRevocato()!=null && !atto.getCodicecifraAttoRevocato().isEmpty()) {
							try {
								attoService.effettuaRevocaAtto(atto,atto.getTipoDeterminazione());
							} catch (ParseException e) {
								throw new RuntimeException(e);
							}
						}
					}
					
//					if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null && atto.getTipoDeterminazione().getCodice().equalsIgnoreCase("MOD-INT")) {
//						if(atto.getCodicecifraAttoRevocato()!=null && !atto.getCodicecifraAttoRevocato().isEmpty()) {
//							try {
//								attoService.effettuaModificaIntegrazioneAtto(atto);
//							} catch (ParseException e) {
//								throw new RuntimeException(e);
//							}
//						}
//					}
				
				}
			}
			
			attoRepository.save(atto);
			
			//Per DG, DC e DPC viene richiamato la trasformazione bozza in atto in fase di assegnazione del numero di adozione
			if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
				
				LocalDate dataEsecutivitaDaInviareAJente = dataAdozione;
				log.info("Imposto data esecutività per l'atto con id " + atto.getId() + " : data esecutività : " + dataEsecutivitaDaInviareAJente);
			
				ContabilitaDto contabilitaDto = new ContabilitaDto();
				contabilitaDto.setCodiceTipoAtto(atto.getTipoAtto().getCodice());
				
				contabilitaDto.setAnnoCreazioneProposta(atto.getDataCreazione().getYear());
				contabilitaDto.setDataCreazioneProposta(atto.getDataCreazione().toDate());
				contabilitaDto.setNumeroProposta(atto.getCodiceCifra().substring(atto.getCodiceCifra().length() - 5));

				contabilitaDto.setAnnoAtto(atto.getDataNumerazione().getYear());
				contabilitaDto.setDataAdozioneAtto(atto.getDataAdozione().toDate());
				contabilitaDto.setNumeroAtto(atto.getNumeroAdozione());
				
				contabilitaDto.setOggetto(atto.getContabileOggetto());
				
				if(atto.getDataEsecutivita() != null) {
					contabilitaDto.setDataEsecutivita(atto.getDataEsecutivita().toDate());
				}
				contabilitaDto.setResponsabileProcedimento(null);
				
				contabilitaService.confirmAtto(contabilitaDto);
				
				if(atto.getIe()!=null && atto.getIe().booleanValue()) {
					if(contabilitaService.esisteBozzaAtto(atto.getTipoAtto().getCodice(),  atto.getNumeroAdozione(),  atto.getDataAdozione().getYear(), false)) {
						contabilitaService.dataEsecutivitaAtto(
								atto.getTipoAtto().getCodice(), 
								atto.getNumeroAdozione(), 
								atto.getDataAdozione().getYear(), 
								dataEsecutivitaDaInviareAJente.toDate());
					}
				}
			}
			
		}
		
	}

}

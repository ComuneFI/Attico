package it.linksmt.assatti.bpm.delegate.impl;

import java.text.ParseException;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.delegate.rollback.DelegateRollbackUtil;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@Transactional
public class DataEsecutivitaDopoPubblicazioneServiceImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(DataEsecutivitaDopoPubblicazioneServiceImpl.class);

	@Inject
	private BpmWrapperUtil bpmWrapperUtil;

	@Inject
	private AttoService attoService;
	
	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {

		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		Boolean rollbackEnabled = rollbackInfo.get("enabled").getAsBoolean();
		
		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		
		boolean dataEsecutivitaGiaImpostata = 
				Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice()) 
				&& (atto.getIe()==null || !atto.getIe().booleanValue()) && atto.getDataEsecutivita()!=null;
		
		if(!dataEsecutivitaGiaImpostata) {
			LocalDate dataEsecutivita = atto.getDataInizioPubblicazionePresunta();
			log.info("DataEsecutivitaDopoPubblicazioneServiceImpl: data Esucutività è:"+dataEsecutivita!=null?dataEsecutivita.toString():"null");		
			if (dataEsecutivita != null) {
				// Valore di Default per data di esecutività
				int daysAdd = 10;
				
				String giorniEsecutivita = WebApplicationProps.getProperty(ConfigPropNames.ATTO_ESECUTIVITA_PLUS_DAYS); 
				if (!StringUtil.isNull(giorniEsecutivita)) {
					daysAdd = Integer.parseInt(giorniEsecutivita);
				}
				
				dataEsecutivita = dataEsecutivita.plusDays(daysAdd);
				
				log.info("Aggiornamento data esecutività per l'atto con id " + atto.getId() + " : data esecutività : " + dataEsecutivita);
				atto.setDataEsecutivita(dataEsecutivita);
				
				atto = attoService.save(atto);
				
				if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null) {
					if(atto.getCodicecifraAttoRevocato()!=null && !atto.getCodicecifraAttoRevocato().isEmpty()) {
						try {
							attoService.effettuaRevocaAtto(atto,atto.getTipoDeterminazione());
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}
				}
				
//				if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null && atto.getTipoDeterminazione().getCodice().equalsIgnoreCase("MOD-INT")) {
//					if(atto.getCodicecifraAttoRevocato()!=null && !atto.getCodicecifraAttoRevocato().isEmpty()) {
//						try {
//							attoService.effettuaModificaIntegrazioneAtto(atto);
//						} catch (ParseException e) {
//							throw new RuntimeException(e);
//						}
//					}
//				}
				
				log.info("Data esecutività per l'atto con id " + atto.getId() + " aggiornata.");
				
				if(rollbackEnabled) {
					rollbackInfo.addProperty("dataEsecutivitaDopoPubblicazione", dataEsecutivita.toString());
				}
			}
		}
	}
}

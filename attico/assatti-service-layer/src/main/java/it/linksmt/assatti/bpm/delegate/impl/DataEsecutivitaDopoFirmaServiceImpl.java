package it.linksmt.assatti.bpm.delegate.impl;

import java.text.ParseException;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.delegate.interfaces.DelegateBusinessLogic;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.service.AttoService;

@Service
@Transactional
public class DataEsecutivitaDopoFirmaServiceImpl implements DelegateBusinessLogic {

	private final Logger log = LoggerFactory.getLogger(DataEsecutivitaDopoFirmaServiceImpl.class);

	@Inject
	private BpmWrapperUtil bpmWrapperUtil;

	@Inject
	private AttoService attoService;

	@Override
	public void executeBusinessLogic(DelegateExecution execution) throws RuntimeException {

		Atto atto = bpmWrapperUtil.getAtto(execution.getProcessBusinessKey());
		
		if(atto.getDataEsecutivita()==null) {
			LocalDate dataEsecutivita = new LocalDate();
			log.info("Aggiornamento data esecutività per l'atto con id " + atto.getId() + " : data esecutività : " + dataEsecutivita);
			atto.setDataEsecutivita(dataEsecutivita);
			atto = attoService.save(atto);
		}else {
			log.info("Data esecutività per l'atto con id " + atto.getId() + " già valorizzata: procedo senza aggiornarla");
		}
		
		if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null) {
			if(atto.getCodicecifraAttoRevocato()!=null && !atto.getCodicecifraAttoRevocato().isEmpty()) {
				try {
					attoService.effettuaRevocaAtto(atto,atto.getTipoDeterminazione());
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		}
//		if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null && atto.getTipoDeterminazione().getCodice().equalsIgnoreCase("MOD-INT")) {
//			if(atto.getCodicecifraAttoRevocato()!=null && !atto.getCodicecifraAttoRevocato().isEmpty()) {
//				try {
//					attoService.effettuaModificaIntegrazioneAtto(atto);
//				} catch (ParseException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}
		
		log.info("Data esecutività per l'atto con id " + atto.getId() + " aggiornata.");
	}
}

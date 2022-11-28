package it.linksmt.assatti.service.converter;

import java.util.Iterator;
import java.util.List;

import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.TipoFinanziamento;
import it.linksmt.assatti.service.dto.AttoSearchDTO;
import it.linksmt.assatti.utility.StringUtil;

@Service
public class AttoSearchConverter {
	
	private static WorkflowServiceWrapper workflowService;
	
	private static BpmWrapperUtil bpmWrapperUtil;
	
	public static void init(WorkflowServiceWrapper workflowService, BpmWrapperUtil bpmWrapperUtil) {
		AttoSearchConverter.workflowService = workflowService;
		AttoSearchConverter.bpmWrapperUtil = bpmWrapperUtil;
	}
	
	public static AttoSearchDTO toDto(Atto atto, boolean withAvanzamento) {
		AttoSearchDTO dto = new AttoSearchDTO();
		dto.setAccessoNegato(false);
		if(atto.getAoo()!=null) {
			dto.setAoo(atto.getAoo().getCodice() + "-" + atto.getAoo().getDescrizione());
		}
		dto.setCodiceCifra(atto.getCodiceCifra());
		dto.setCodicecifraAttoRevocato(atto.getCodicecifraAttoRevocato());
		dto.setCreatedBy(atto.getCreatedBy());
		dto.setDataAdozione(atto.getDataAdozione());
		dto.setDataCreazione(atto.getDataCreazione());
		dto.setDataEsecutivita(atto.getDataEsecutivita());
		dto.setFineIterDate(atto.getFineIterDate());
		dto.setFinePubblicazioneAlbo(atto.getFinePubblicazioneAlbo());
		dto.setId(atto.getId());
		dto.setInizioPubblicazioneAlbo(atto.getInizioPubblicazioneAlbo());
		dto.setCodiceCup(atto.getCodiceCup());
		dto.setCodiceCig(atto.getCodiceCig());
		if(withAvanzamento) {
			List<Task> tasks = workflowService.getTaskByIdAtto(atto.getId());
			if(tasks!=null && tasks.size() > 0) {
				for(Task task : tasks) {
					dto.setNoteAvanzamento(StringUtil.IT_DATE_FORMAT.format(task.getCreateTime()));
					if(task.getAssignee()!=null && !task.getAssignee().isEmpty()) {
						dto.setCreatedByAvanzamento(bpmWrapperUtil.getNominativo(task.getAssignee()));
						break;
					}
				}
			}
			/*
			List<StatoAttoDTO> statos = workflowService.getStatoAtto(atto);
			StatoAttoDTO stato = null;
			if(statos != null && statos.size() > 0) {
				stato = statos.get(0);
			}
			if (stato != null) {
				if (!StringUtil.isNull(stato.getNominativo())) {
					dto.setCreatedByAvanzamento(stato.getNominativo());
				}
				if (stato.getDatacarico() != null) {
					dto.setNoteAvanzamento(StringUtil.IT_DATE_FORMAT.format(stato.getDatacarico().toDate()));
					
				}
			}
			*/
		}
		dto.setNumeroAdozione(atto.getNumeroAdozione());
		dto.setOggetto(atto.getOggetto());
		dto.setOmissisLink("");
		dto.setStato(atto.getStato());
		if(atto.getTipoIter()!=null) {
			dto.setTipoIter(atto.getTipoIter().getDescrizione());
		}
		String tipiFinanziamento = "";
		if(atto.getHasTipoFinanziamenti()!=null) {
			for (TipoFinanziamento tipoFinanziamento :  atto.getHasTipoFinanziamenti()) {
				if(!tipiFinanziamento.isEmpty()) {
					tipiFinanziamento+=", ";
				}
				tipiFinanziamento += tipoFinanziamento.getDescrizione();
				
			}
		}
		
		dto.setTipiFinanziamento(tipiFinanziamento);
		
		return dto;
	}
}

package it.linksmt.assatti.bpm.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;

public class ListaDecisioni {

	private List<DecisioneWorkflowDTO> listaDecisioni = new ArrayList<DecisioneWorkflowDTO>();
	
	public String export() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(listaDecisioni);
	}
	
	public DecisioneWorkflowDTO addDecisione(String descrizione, boolean completaTask) {
		return addDecisione(descrizione, null, null, null, completaTask);
	}

	public DecisioneWorkflowDTO addDecisione(String descrizione, String codiceAzioneUtente, boolean completaTask) {
		return addDecisione(descrizione, null, null, codiceAzioneUtente, completaTask);
	}
	
	public DecisioneWorkflowDTO addDecisione(String descrizione, 
			String variableSet, String variableValue,
			boolean completaTask) {
		return addDecisione(descrizione, variableSet,  variableValue, null, completaTask);
	}
	
	public DecisioneWorkflowDTO addEditIter(
			String descrizione, 
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask) {
		DecisioneWorkflowDTO decisione = this.addDecisioneSempreAttiva(descrizione, variableSet,variableValue, codiceAzioneUtente, completaTask);
		decisione.setLoggaAzione(true);
		return decisione;
	}
	
	public DecisioneWorkflowDTO addDecisioneSempreAttiva(
			String descrizione, 
			String tipoParere,
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask) {
		DecisioneWorkflowDTO decisione = addDecisione(descrizione, tipoParere, null, variableSet,  variableValue, codiceAzioneUtente, completaTask);
		decisione.setSempreAttiva(true);
		return decisione;
	}
	
	public DecisioneWorkflowDTO addDecisioneSempreAttiva(
			String descrizione, 
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask) {
		DecisioneWorkflowDTO decisione = addDecisione(descrizione, null, null, variableSet,  variableValue, codiceAzioneUtente, completaTask);
		decisione.setSempreAttiva(true);
		return decisione;
	}

	public DecisioneWorkflowDTO addDecisione(
			String descrizione, 
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask) {
		return addDecisione(descrizione, null, null, variableSet,  variableValue, codiceAzioneUtente, completaTask);
	}
	
	public DecisioneWorkflowDTO addDecisioneConDescrizioneAlternativa(
			String descrizione, 
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask,
			String descrizioneAlternativa) {
		return addDecisioneConDescrizioneAlternativa(descrizione, null, null, variableSet,  variableValue, codiceAzioneUtente, completaTask,descrizioneAlternativa);
	}
	
	public DecisioneWorkflowDTO addDecisione(
			String descrizione, 
			String tipoParere,
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask) {
		DecisioneWorkflowDTO decisione = addDecisione(descrizione, tipoParere, null, variableSet,  variableValue, codiceAzioneUtente, completaTask);
		if(CodiciAzioniUtente.RITIRO_FASE_ISTRUTTORIA.equals(codiceAzioneUtente)) {
			decisione.setSempreAttiva(Boolean.TRUE);
		}
		return decisione;
	}
	

	
	public DecisioneWorkflowDTO addParere(
			String descrizione, 
			String tipoParere, 
			String variableSet,
			String codiceAzioneUtente,
			boolean completaTask) {
		return addDecisione(descrizione, tipoParere, null, variableSet,  null, codiceAzioneUtente, completaTask);
	}
	
	public DecisioneWorkflowDTO addParere(
			String descrizione, 
			String tipoParere,
			String origineParere,
			String variableSet,
			String codiceAzioneUtente,
			boolean completaTask) {
		return addDecisione(descrizione, tipoParere, origineParere, variableSet,  null, codiceAzioneUtente, completaTask);
	}
	
	public DecisioneWorkflowDTO addParereLoggaAzione(
			String descrizione, 
			String tipoParere,
			String origineParere,
			String variableSet,
			String codiceAzioneUtente,
			boolean completaTask) {
		DecisioneWorkflowDTO decisione = addDecisione(descrizione, tipoParere, origineParere, variableSet,  null, codiceAzioneUtente, completaTask);
		decisione.setLoggaAzione(true);
		return decisione;
	}
	
	public DecisioneWorkflowDTO addVisto(
			String descrizione, 
			String tipoParere,
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask) {
		return addDecisione(descrizione, tipoParere, null, variableSet,  variableValue, codiceAzioneUtente, completaTask);
	}
	
	public DecisioneWorkflowDTO addVisto(
			String descrizione, 
			String tipoParere,
			String variableSet,
			String variableValue,
			boolean completaTask) {
		return addDecisione(descrizione, tipoParere, null, variableSet,  variableValue, null, completaTask);
	}
	
	private DecisioneWorkflowDTO addDecisione(
			String descrizione,
			String tipoParere,
			String origineParere,
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask) {
		
		DecisioneWorkflowDTO decisione = new DecisioneWorkflowDTO();
		decisione.setTipoParere(tipoParere);
		decisione.setOrigineParere(origineParere);
		decisione.setDescrizione(descrizione);
		decisione.setVariableSet(variableSet);
		decisione.setVariableValue(variableValue);
		decisione.setCodiceAzioneUtente(codiceAzioneUtente);
		decisione.setCompletaTask(completaTask);
		
		listaDecisioni.add(decisione);
		return decisione;
	}
	
	public DecisioneWorkflowDTO addDecisioneConDescrizioneAlternativa(
			String descrizione,
			String tipoParere,
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask,
			String descrizioneAlternativa) {
		
		DecisioneWorkflowDTO decisione = new DecisioneWorkflowDTO();
		decisione.setTipoParere(tipoParere);
		decisione.setOrigineParere(null);
		decisione.setDescrizione(descrizione);
		decisione.setVariableSet(variableSet);
		decisione.setVariableValue(variableValue);
		decisione.setCodiceAzioneUtente(codiceAzioneUtente);
		decisione.setCompletaTask(completaTask);
		decisione.setDescrizioneAlternativa(descrizioneAlternativa);
		decisione.setLoggaAzione(true);
		listaDecisioni.add(decisione);
		return decisione;
	}
	
	public DecisioneWorkflowDTO addDecisioneConDescrizioneAlternativa(
			String descrizione,
			String tipoParere,
			String origineParere,
			String variableSet,
			String variableValue,
			String codiceAzioneUtente,
			boolean completaTask,
			String descrizioneAlternativa) {
		
		DecisioneWorkflowDTO decisione = new DecisioneWorkflowDTO();
		decisione.setTipoParere(tipoParere);
		decisione.setOrigineParere(origineParere);
		decisione.setDescrizione(descrizione);
		decisione.setVariableSet(variableSet);
		decisione.setVariableValue(variableValue);
		decisione.setCodiceAzioneUtente(codiceAzioneUtente);
		decisione.setCompletaTask(completaTask);
		decisione.setDescrizioneAlternativa(descrizioneAlternativa);
		decisione.setLoggaAzione(true);
		listaDecisioni.add(decisione);
		return decisione;
	}
	
	public DecisioneWorkflowDTO addParereConDescrizioneAlternativa(
			String descrizione, 
			String tipoParere,
			String origineParere,
			String variableSet,
			String codiceAzioneUtente,
			boolean completaTask,
			String descrizioneAlternativa) {
		return addDecisioneConDescrizioneAlternativa(descrizione, tipoParere, origineParere, variableSet,  null, codiceAzioneUtente, completaTask, descrizioneAlternativa);
	}
}

package it.linksmt.assatti.bpm.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.linksmt.assatti.bpm.dto.AssegnazioneIncaricoDTO;

public class ListaAssegnazioniIncarichi {
	
	private List<AssegnazioneIncaricoDTO> listaIncarichi = new ArrayList<AssegnazioneIncaricoDTO>();

	public String export() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(listaIncarichi);
	}
	
	public void addAssegnazioneIncarico(String codiceConfigurazione, String varAssegnatario) {
		AssegnazioneIncaricoDTO assegnazione = new AssegnazioneIncaricoDTO();
		assegnazione.setCodiceConfigurazione(codiceConfigurazione);
		assegnazione.setVarAssegnatario(varAssegnatario);
				
		listaIncarichi.add(assegnazione);
	}
}

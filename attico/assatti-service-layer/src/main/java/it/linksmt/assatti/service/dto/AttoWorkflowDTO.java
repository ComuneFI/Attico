package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Parere;

public class AttoWorkflowDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Atto atto;
	private Map<String, Object> campi = new HashMap<String, Object>();
	private Parere parere;
	private DecisioneWorkflowDTO decisione;
	private List<ConfigurazioneIncaricoDto> incarichi;
	private List<Long> modelliHtmlIds;
	private FirmaRemotaDTO dtoFdr;
	
	public AttoWorkflowDTO() {
	}

	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public Map<String, Object> getCampi() {
		return campi;
	}

	public void setCampi(Map<String, Object> campi) {
		this.campi = campi;
	}

	public Parere getParere() {
		return parere;
	}

	public void setParere(Parere parere) {
		this.parere = parere;
	}

	public DecisioneWorkflowDTO getDecisione() {
		return decisione;
	}

	public void setDecisione(DecisioneWorkflowDTO decisione) {
		this.decisione = decisione;
	}

	public List<ConfigurazioneIncaricoDto> getIncarichi() {
		return incarichi;
	}

	public void setIncarichi(List<ConfigurazioneIncaricoDto> incarichi) {
		this.incarichi = incarichi;
	}

	public List<Long> getModelliHtmlIds() {
		return modelliHtmlIds;
	}

	public void setModelliHtmlIds(List<Long> modelliHtmlIds) {
		this.modelliHtmlIds = modelliHtmlIds;
	}

	public FirmaRemotaDTO getDtoFdr() {
		return dtoFdr;
	}

	public void setDtoFdr(FirmaRemotaDTO dtoFdr) {
		this.dtoFdr = dtoFdr;
	}
}

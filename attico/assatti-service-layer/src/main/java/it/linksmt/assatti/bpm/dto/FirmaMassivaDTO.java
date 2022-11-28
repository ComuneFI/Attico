package it.linksmt.assatti.bpm.dto;

import java.util.List;


public class FirmaMassivaDTO {
	
	private String type;
	private List<Long> modelli;
	private List<Long> modelliSolaGenerazione;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Long> getModelli() {
		return modelli;
	}
	public void setModelli(List<Long> modelli) {
		this.modelli = modelli;
	}
	public List<Long> getModelliSolaGenerazione() {
		return modelliSolaGenerazione;
	}
	public void setModelliSolaGenerazione(List<Long> modelliSolaGenerazione) {
		this.modelliSolaGenerazione = modelliSolaGenerazione;
	}
}

package it.linksmt.assatti.service.dto;


public class AssessoratoSearchDTO {
	private String id;
	private String codice;
	private String responsabile;
	private String denominazione;
	private String qualifica;
	private String stato;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getResponsabile() {
		return responsabile;
	}
	public void setResponsabile(String responsabile) {
		this.responsabile = responsabile;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	public String getQualifica() {
		return qualifica;
	}
	public void setQualifica(String qualifica) {
		this.qualifica = qualifica;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}	
}

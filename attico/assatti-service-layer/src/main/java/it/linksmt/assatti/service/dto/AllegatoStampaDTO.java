package it.linksmt.assatti.service.dto;

import java.io.Serializable;

public class AllegatoStampaDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String progressivo;
	private String descrizione;
	private String tipo;
	private String annotazione;
	public String getProgressivo() {
		return progressivo;
	}
	public void setProgressivo(String progressivo) {
		this.progressivo = progressivo;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getAnnotazione() {
		return annotazione;
	}
	public void setAnnotazione(String annotazione) {
		this.annotazione = annotazione;
	}
	
	
}

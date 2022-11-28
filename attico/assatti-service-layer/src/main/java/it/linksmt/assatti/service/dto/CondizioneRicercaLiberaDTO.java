package it.linksmt.assatti.service.dto;

import java.io.Serializable;


import javax.validation.constraints.NotNull;

public class CondizioneRicercaLiberaDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotNull
	private String campo;
	
	@NotNull
	private String tipoCampo;
	
	@NotNull
	private String condizione;
	
	@NotNull
	private String valore;
	
	private String relazioneAltroCampo;

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public String getCondizione() {
		return condizione;
	}

	public void setCondizione(String condizione) {
		this.condizione = condizione;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	public String getRelazioneAltroCampo() {
		return relazioneAltroCampo;
	}

	public void setRelazioneAltroCampo(String relazioneAltroCampo) {
		this.relazioneAltroCampo = relazioneAltroCampo;
	}

	public String getTipoCampo() {
		return tipoCampo;
	}

	public void setTipoCampo(String tipoCampo) {
		this.tipoCampo = tipoCampo;
	}

	
}

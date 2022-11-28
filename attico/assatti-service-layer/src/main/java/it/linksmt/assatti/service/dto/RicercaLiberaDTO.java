package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

public class RicercaLiberaDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String aooId;
	
	@NotNull
	private List<CondizioneRicercaLiberaDTO> campiWhere;
	
	@NotNull
	private List<String> campiSelect;	
	
	@NotNull
	private String ordinamento;
	
	@NotNull
	private Long gruppoRuolo;
	
	@NotNull
	private Long profiloId;
	
	@NotNull
	private String tipoOrdinamento;

	public String getTipoOrdinamento() {
		return tipoOrdinamento;
	}

	public void setTipoOrdinamento(String tipoOrdinamento) {
		this.tipoOrdinamento = tipoOrdinamento;
	}

	public List<CondizioneRicercaLiberaDTO> getCampiWhere() {
		return campiWhere;
	}

	public void setCampiWhere(List<CondizioneRicercaLiberaDTO> campiWhere) {
		this.campiWhere = campiWhere;
	}

	public List<String> getCampiSelect() {
		return campiSelect;
	}

	public void setCampiSelect(List<String> campiSelect) {
		this.campiSelect = campiSelect;
	}

	public String getOrdinamento() {
		return ordinamento;
	}

	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}
	
	public Long getGruppoRuolo() {
		return gruppoRuolo;
	}

	public void setGruppoRuolo(Long gruppoRuolo) {
		this.gruppoRuolo = gruppoRuolo;
	}
	
	public Long getProfiloId() {
		return profiloId;
	}

	public void setProfiloId(Long profiloId) {
		this.profiloId = profiloId;
	}
	
	public String getAooId(){
		return this.aooId;
	}
	
	public void setAooId(String aooId){
		this.aooId = aooId;
	}
	
}

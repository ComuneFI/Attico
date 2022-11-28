package it.linksmt.assatti.service.dto;

import java.io.Serializable;

public class SedutaAnnullaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long sedutaId;
	private Long modelloId;
	private Long profiloId;
	private Long profiloSottoscrittoreId;
	
	public Long getSedutaId() {
		return sedutaId;
	}
	public void setSedutaId(Long sedutaId) {
		this.sedutaId = sedutaId;
	}
	public Long getModelloId() {
		return modelloId;
	}
	public void setModelloId(Long modelloId) {
		this.modelloId = modelloId;
	}
	public Long getProfiloId() {
		return profiloId;
	}
	public void setProfiloId(Long profiloId) {
		this.profiloId = profiloId;
	}
	public Long getProfiloSottoscrittoreId() {
		return profiloSottoscrittoreId;
	}
	public void setProfiloSottoscrittoreId(Long profiloSottoscrittoreId) {
		this.profiloSottoscrittoreId = profiloSottoscrittoreId;
	}
}

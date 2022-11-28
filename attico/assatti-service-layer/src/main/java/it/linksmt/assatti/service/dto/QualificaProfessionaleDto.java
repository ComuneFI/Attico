package it.linksmt.assatti.service.dto;

import java.io.Serializable;

/**
 * Domain class for QualificaProfessionaleDto.
 */
public class QualificaProfessionaleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String denominazione;

	private Boolean enabled;

	/*
	 * Get & Set
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}

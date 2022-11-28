package it.linksmt.assatti.service.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

public class AvanzamentoCriteriaDTO implements Serializable {
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataAttivita;
	
	private String atto;
	
	private String stato;
	
	private String attivita;
	
	private String utente;
	
	@NotNull
	private String ordinamento;
	
	@NotNull
	private String tipoOrinamento;

	
	public LocalDate getDataAttivita() {
		return dataAttivita;
	}

	public void setDataAttivita(LocalDate dataAttivita) {
		this.dataAttivita = dataAttivita;
	}

	public String getAtto() {
		return atto;
	}

	public void setAtto(String atto) {
		this.atto = atto;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getAttivita() {
		return attivita;
	}

	public void setAttivita(String attivita) {
		this.attivita = attivita;
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getOrdinamento() {
		return ordinamento;
	}

	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}

	public String getTipoOrinamento() {
		return tipoOrinamento;
	}

	public void setTipoOrinamento(String tipoOrinamento) {
		this.tipoOrinamento = tipoOrinamento;
	}

	
	
}

package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

public class DelegaDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataValiditaInizio;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataValiditaFine;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataCreazione;
	
	private Boolean enabled;

	private Profilo profiloDelegante;
	
	private List<Profilo> delegati;

	/*
	 * Get & Set
	 */

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public LocalDate getDataValiditaInizio() {
		return dataValiditaInizio;
	}

	public void setDataValiditaInizio(LocalDate dataValiditaInizio) {
		this.dataValiditaInizio = dataValiditaInizio;
	}

	public LocalDate getDataValiditaFine() {
		return dataValiditaFine;
	}

	public void setDataValiditaFine(LocalDate dataValiditaFine) {
		this.dataValiditaFine = dataValiditaFine;
	}

	public Profilo getProfiloDelegante() {
		return profiloDelegante;
	}
	
	public void setProfiloDelegante(Profilo profiloDelegante) {
		this.profiloDelegante = profiloDelegante;
	}

	public LocalDate getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public List<Profilo> getDelegati() {
		return delegati;
	}

	public void setDelegati(List<Profilo> delegati) {
		this.delegati = delegati;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}

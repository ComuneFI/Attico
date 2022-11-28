package it.linksmt.assatti.service.dto;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AttiOdgCriteriaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String statoProposta;
	private String codiceCifra;
	private Long profiloId;
	private String numSeduta;
	private String tipoSeduta;
	private String organo;
	private String tipoOdg;
	private String oggetto;
	private String esito;
	private Boolean modificatoInGiunta;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime dataOraSedutaDa;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime dataOraSedutaA;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataAdozione;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataEsecutivitaDa;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataEsecutivitaA;
	
	private String numeroAdozione;
	
	/**
	 * Filtro da utiizzare per recuperare le sedute in cui il dato profilo sia intervenuto 
	 * come Presidente o Segretario della seduta di giunta o come loro delegato nella firma 
	 * di uno dei documenti (verbale, resoconto, odg ecc.) 
	 */
	private Long sottoscrittoreProfiloId;
	
	private String sottoscrittoreNome;
	
	
	public Long getProfiloId() {
		return profiloId;
	}

	public void setProfiloId(Long profiloId) {
		this.profiloId = profiloId;
	}

	public String getTipoSeduta() {
		return tipoSeduta;
	}

	public void setTipoSeduta(String tipoSeduta) {
		this.tipoSeduta = tipoSeduta;
	}
	
	public String getStatoProposta() {
		return statoProposta;
	}

	public void setStatoProposta(String statoProposta) {
		this.statoProposta = statoProposta;
	}

	public String getCodiceCifra() {
		return codiceCifra;
	}

	public void setCodiceCifra(String codiceCifra) {
		this.codiceCifra = codiceCifra;
	}

	public String getNumSeduta() {
		return numSeduta;
	}

	public void setNumSeduta(String numSeduta) {
		this.numSeduta = numSeduta;
	}

	public String getTipoOdg() {
		return tipoOdg;
	}

	public void setTipoOdg(String tipoOdg) {
		this.tipoOdg = tipoOdg;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}

	public Boolean getModificatoInGiunta() {
		return modificatoInGiunta;
	}

	public void setModificatoInGiunta(Boolean modificatoInGiunta) {
		this.modificatoInGiunta = modificatoInGiunta;
	}

	public String getSottoscrittoreNome() {
		return sottoscrittoreNome;
	}

	public void setSottoscrittoreNome(String sottoscrittoreNome) {
		this.sottoscrittoreNome = sottoscrittoreNome;
	}

	public DateTime getDataOraSedutaDa() {
		return dataOraSedutaDa;
	}

	public void setDataOraSedutaDa(DateTime dataOraSedutaDa) {
		this.dataOraSedutaDa = dataOraSedutaDa;
	}

	public DateTime getDataOraSedutaA() {
		return dataOraSedutaA;
	}

	public void setDataOraSedutaA(DateTime dataOraSedutaA) {
		this.dataOraSedutaA = dataOraSedutaA;
	}

	public LocalDate getDataEsecutivitaDa() {
		return dataEsecutivitaDa;
	}

	public void setDataEsecutivitaDa(LocalDate dataEsecutivitaDa) {
		this.dataEsecutivitaDa = dataEsecutivitaDa;
	}

	public LocalDate getDataEsecutivitaA() {
		return dataEsecutivitaA;
	}

	public void setDataEsecutivitaA(LocalDate dataEsecutivitaA) {
		this.dataEsecutivitaA = dataEsecutivitaA;
	}

	public Long getSottoscrittoreProfiloId() {
		return sottoscrittoreProfiloId;
	}
	public void setSottoscrittoreProfiloId(Long sottoscrittoreProfiloId) {
		this.sottoscrittoreProfiloId = sottoscrittoreProfiloId;
	}

	public LocalDate getDataAdozione() {
		return dataAdozione;
	}

	public void setDataAdozione(LocalDate dataAdozione) {
		this.dataAdozione = dataAdozione;
	}

	public String getNumeroAdozione() {
		return numeroAdozione;
	}

	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}	
}

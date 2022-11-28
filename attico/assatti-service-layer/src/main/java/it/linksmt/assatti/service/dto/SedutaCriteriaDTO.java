package it.linksmt.assatti.service.dto;

import java.io.Serializable;

import org.joda.time.DateTime;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SedutaCriteriaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stato;
	private String type;
	private Long profiloId;
	private String numOdg;
	private String tipoSeduta;
	private String presidente;
	private Boolean decorsiTermini;
	private Boolean estremiVariati;
	private String statoDocumento;
	private String sottoscrittoreDocumento;
	private String organo;
	private Boolean ecludiInfoAggiuntive;
	
	/**
	 * Filtro da utiizzare per recuperare le sedute in cui il dato profilo sia intervenuto 
	 * come Presidente o Segretario della seduta di giunta o come loro delegato nella firma 
	 * di uno dei documenti (verbale, resoconto, odg ecc.) 
	 */
	private Long sottoscrittoreProfiloId;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime primaConvInizioDa;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime primaConvInizioA;
	
	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}
	
	public Long getProfiloId() {
		return profiloId;
	}

	public void setProfiloId(Long profiloId) {
		this.profiloId = profiloId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTipoSeduta() {
		return tipoSeduta;
	}

	public void setTipoSeduta(String tipoSeduta) {
		this.tipoSeduta = tipoSeduta;
	}
	
	public String getNumOdg() {
		return numOdg;
	}

	public void setNumOdg(String numOdg) {
		this.numOdg = numOdg;
	}

	public String getPresidente() {
		return presidente;
	}

	public void setPresidente(String presidente) {
		this.presidente = presidente;
	}

	public Boolean getDecorsiTermini() {
		return decorsiTermini;
	}

	public void setDecorsiTermini(Boolean decorsiTermini) {
		this.decorsiTermini = decorsiTermini;
	}

	public Boolean getEstremiVariati() {
		return estremiVariati;
	}

	public void setEstremiVariati(Boolean estremiVariati) {
		this.estremiVariati = estremiVariati;
	}

	public DateTime getPrimaConvInizioDa() {
		return primaConvInizioDa;
	}

	public void setPrimaConvInizioDa(DateTime primaConvInizioDa) {
		this.primaConvInizioDa = primaConvInizioDa;
	}

	public DateTime getPrimaConvInizioA() {
		return primaConvInizioA;
	}

	public void setPrimaConvInizioA(DateTime primaConvInizioA) {
		this.primaConvInizioA = primaConvInizioA;
	}
	public Long getSottoscrittoreProfiloId() {
		return sottoscrittoreProfiloId;
	}
	public void setSottoscrittoreProfiloId(Long sottoscrittoreProfiloId) {
		this.sottoscrittoreProfiloId = sottoscrittoreProfiloId;
	}

	public String getStatoDocumento() {
		return statoDocumento;
	}

	public void setStatoDocumento(String statoDocumento) {
		this.statoDocumento = statoDocumento;
	}

	public String getSottoscrittoreDocumento() {
		return sottoscrittoreDocumento;
	}

	public void setSottoscrittoreDocumento(String sottoscrittoreDocumento) {
		this.sottoscrittoreDocumento = sottoscrittoreDocumento;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}

	public Boolean getEcludiInfoAggiuntive() {
		return ecludiInfoAggiuntive;
	}

	public void setEcludiInfoAggiuntive(Boolean ecludiInfoAggiuntive) {
		this.ecludiInfoAggiuntive = ecludiInfoAggiuntive;
	}
	
}

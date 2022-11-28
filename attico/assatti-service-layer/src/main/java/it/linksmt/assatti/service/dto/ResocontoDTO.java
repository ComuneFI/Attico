package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.Composizione;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;


public class ResocontoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Composizione composizione;
	private String esito;
	private Boolean isAttoModificatoInGiunta;
	
	//private ComponentiGiunta presidenteInizio;
	//private ComponentiGiunta segretarioInizio;
	
	private ComponentiGiunta presidenteFine;
	private ComponentiGiunta segretarioFine;
	
	private ComponentiGiunta presidenteIE;
	private ComponentiGiunta segretarioIE;
	private ComponentiGiunta sindaco;
	
	private List<ComponentiGiunta> componenti;
	private List<ComponentiGiunta> scrutatori;
	private List<ComponentiGiunta> scrutatoriIE;
	
	private Integer numPresenti;
	private Integer numAssenti;
	private Integer numFavorevoli;
	private Integer numContrari;
	private Integer numAstenuti;
	private Integer numNpv;
	
	private Boolean votazioneSegreta;
	private Boolean votazioneIE;
	private Boolean approvataIE;
	
	private String dichiarazioniVoto;
	private String sedutaConvocata;
	
	private String attoPresentato;
	
	private Boolean ie;
	
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataDiscussione;
	
	public String getEsito() {
		return esito;
	}
	public void setEsito(String esito) {
		this.esito = esito;
	}
	public List<ComponentiGiunta> getComponenti() {
		return componenti;
	}
	public void setComponenti(List<ComponentiGiunta> componenti) {
		this.componenti = componenti;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getDataDiscussione() {
		return dataDiscussione;
	}
	public void setDataDiscussione(LocalDate dataDiscussione) {
		this.dataDiscussione = dataDiscussione;
	}
	public Boolean getIsAttoModificatoInGiunta() {
		return isAttoModificatoInGiunta;
	}
	public void setIsAttoModificatoInGiunta(Boolean isAttoModificatoInGiunta) {
		this.isAttoModificatoInGiunta = isAttoModificatoInGiunta;
	}
//	public ComponentiGiunta getPresidenteInizio() {
//		return presidenteInizio;
//	}
//	public void setPresidenteInizio(ComponentiGiunta presidenteInizio) {
//		this.presidenteInizio = presidenteInizio;
//	}
//	public ComponentiGiunta getSegretarioInizio() {
//		return segretarioInizio;
//	}
//	public void setSegretarioInizio(ComponentiGiunta segretarioInizio) {
//		this.segretarioInizio = segretarioInizio;
//	}
	public ComponentiGiunta getPresidenteFine() {
		return presidenteFine;
	}
	public void setPresidenteFine(ComponentiGiunta presidenteFine) {
		this.presidenteFine = presidenteFine;
	}
	public ComponentiGiunta getSegretarioFine() {
		return segretarioFine;
	}
	public void setSegretarioFine(ComponentiGiunta segretarioFine) {
		this.segretarioFine = segretarioFine;
	}
	public List<ComponentiGiunta> getScrutatori() {
		return scrutatori;
	}
	public void setScrutatori(List<ComponentiGiunta> scrutatori) {
		this.scrutatori = scrutatori;
	}
	public List<ComponentiGiunta> getScrutatoriIE() {
		return scrutatoriIE;
	}
	public void setScrutatoriIE(List<ComponentiGiunta> scrutatoriIE) {
		this.scrutatoriIE = scrutatoriIE;
	}
	public Integer getNumPresenti() {
		return numPresenti;
	}
	public void setNumPresenti(Integer numPresenti) {
		this.numPresenti = numPresenti;
	}
	public Integer getNumAssenti() {
		return numAssenti;
	}
	public void setNumAssenti(Integer numAssenti) {
		this.numAssenti = numAssenti;
	}
	public Integer getNumFavorevoli() {
		return numFavorevoli;
	}
	public void setNumFavorevoli(Integer numFavorevoli) {
		this.numFavorevoli = numFavorevoli;
	}
	public Integer getNumContrari() {
		return numContrari;
	}
	public void setNumContrari(Integer numContrari) {
		this.numContrari = numContrari;
	}
	public Integer getNumAstenuti() {
		return numAstenuti;
	}
	public void setNumAstenuti(Integer numAstenuti) {
		this.numAstenuti = numAstenuti;
	}
	public Integer getNumNpv() {
		return numNpv;
	}
	public void setNumNpv(Integer numNpv) {
		this.numNpv = numNpv;
	}
	public String getDichiarazioniVoto() {
		return dichiarazioniVoto;
	}
	public void setDichiarazioniVoto(String dichiarazioniVoto) {
		this.dichiarazioniVoto = dichiarazioniVoto;
	}
	public String getSedutaConvocata() {
		return sedutaConvocata;
	}
	public void setSedutaConvocata(String sedutaConvocata) {
		this.sedutaConvocata = sedutaConvocata;
	}
	public String getAttoPresentato() {
		return attoPresentato;
	}
	public void setAttoPresentato(String attoPresentato) {
		this.attoPresentato = attoPresentato;
	}
	public Boolean getVotazioneSegreta() {
		return votazioneSegreta;
	}
	public void setVotazioneSegreta(Boolean votazioneSegreta) {
		this.votazioneSegreta = votazioneSegreta;
	}
	public Boolean getVotazioneIE() {
		return votazioneIE;
	}
	public void setVotazioneIE(Boolean votazioneIE) {
		this.votazioneIE = votazioneIE;
	}
	public Boolean getApprovataIE() {
		return approvataIE;
	}
	public void setApprovataIE(Boolean approvataIE) {
		this.approvataIE = approvataIE;
	}
	public ComponentiGiunta getPresidenteIE() {
		return presidenteIE;
	}
	public void setPresidenteIE(ComponentiGiunta presidenteIE) {
		this.presidenteIE = presidenteIE;
	}
	public ComponentiGiunta getSegretarioIE() {
		return segretarioIE;
	}
	public void setSegretarioIE(ComponentiGiunta segretarioIE) {
		this.segretarioIE = segretarioIE;
	}
	public ComponentiGiunta getSindaco() {
		return sindaco;
	}
	public void setSindaco(ComponentiGiunta sindaco) {
		this.sindaco = sindaco;
	}
	public Boolean getIe() {
		return ie;
	}
	public void setIe(Boolean ie) {
		this.ie = ie;
	}
	public Composizione getComposizione() {
		return composizione;
	}
	public void setComposizione(Composizione composizione) {
		this.composizione = composizione;
	}
}

package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoricoAttoCriteriaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3825954530118961171L;
	
	private Long profiloId;
	private String aooCodice;
	private String aooDescr;
	@NotNull
	private List<String> tipiAttoCodici;
	private String ordinamento;
	private String tipoOrdinamento;
	
	private String codiceCifra;
	private String area;
	private String lavorazioneEffettuata;
	private String oggetto;
	private String viewtype;
	private String anno;
	private String stato;
	private String tipoAtto;
	private String tipoIter;
	private String tipoAdempimento;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataAdozione;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataAdozioneA;
	private String numeroAdozione;
	private String codiceLavorazione;
	private String esitoVerificaContabile;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataEsecutivita;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataEsecutivitaA;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataInizioAffissione;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataInizioAffissioneA;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataFineAffissione;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataFineAffissioneA;
	private String redigente;
	private String regolamento;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataRiunioneGiunta;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataRiunioneGiuntaA;
	private String oraRiunioneGiunta;
	private String annullato;
	private String tipoRiunioneGiunta;
	private String numRiunioneGiunta;
	
	public Long getProfiloId() {
		return profiloId;
	}
	public void setProfiloId(Long profiloId) {
		this.profiloId = profiloId;
	}
	public String getAooCodice() {
		return aooCodice;
	}
	public void setAooCodice(String aooCodice) {
		this.aooCodice = aooCodice;
	}
	public List<String> getTipiAttoCodici() {
		return tipiAttoCodici;
	}
	public void setTipiAttoCodici(List<String> tipiAttoCodici) {
		this.tipiAttoCodici = tipiAttoCodici;
	}
	public String getOrdinamento() {
		return ordinamento;
	}
	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}
	public String getTipoOrdinamento() {
		return tipoOrdinamento;
	}
	public void setTipoOrdinamento(String tipoOrdinamento) {
		this.tipoOrdinamento = tipoOrdinamento;
	}
	public String getCodiceCifra() {
		return codiceCifra;
	}
	public void setCodiceCifra(String codiceCifra) {
		this.codiceCifra = codiceCifra;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getLavorazioneEffettuata() {
		return lavorazioneEffettuata;
	}
	public void setLavorazioneEffettuata(String lavorazioneEffettuata) {
		this.lavorazioneEffettuata = lavorazioneEffettuata;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getViewtype() {
		return viewtype;
	}
	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}
	public String getAnno() {
		return anno;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	public String getTipoAtto() {
		return tipoAtto;
	}
	public void setTipoAtto(String tipoAtto) {
		this.tipoAtto = tipoAtto;
	}
	public String getTipoIter() {
		return tipoIter;
	}
	public void setTipoIter(String tipoIter) {
		this.tipoIter = tipoIter;
	}
	public LocalDate getDataAdozione() {
		return dataAdozione;
	}
	public void setDataAdozione(LocalDate dataAdozione) {
		this.dataAdozione = dataAdozione;
	}
	public LocalDate getDataAdozioneA() {
		return dataAdozioneA;
	}
	public void setDataAdozioneA(LocalDate dataAdozioneA) {
		this.dataAdozioneA = dataAdozioneA;
	}
	public String getNumeroAdozione() {
		return numeroAdozione;
	}
	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}
	public String getCodiceLavorazione() {
		return codiceLavorazione;
	}
	public void setCodiceLavorazione(String codiceLavorazione) {
		this.codiceLavorazione = codiceLavorazione;
	}
	public String getTipoAdempimento() {
		return tipoAdempimento;
	}
	public void setTipoAdempimento(String tipoAdempimento) {
		this.tipoAdempimento = tipoAdempimento;
	}
	public String getEsitoVerificaContabile() {
		return esitoVerificaContabile;
	}
	public void setEsitoVerificaContabile(String esitoVerificaContabile) {
		this.esitoVerificaContabile = esitoVerificaContabile;
	}
	public LocalDate getDataEsecutivita() {
		return dataEsecutivita;
	}
	public void setDataEsecutivita(LocalDate dataEsecutivita) {
		this.dataEsecutivita = dataEsecutivita;
	}
	public LocalDate getDataEsecutivitaA() {
		return dataEsecutivitaA;
	}
	public void setDataEsecutivitaA(LocalDate dataEsecutivitaA) {
		this.dataEsecutivitaA = dataEsecutivitaA;
	}
	public LocalDate getDataInizioAffissione() {
		return dataInizioAffissione;
	}
	public void setDataInizioAffissione(LocalDate dataInizioAffissione) {
		this.dataInizioAffissione = dataInizioAffissione;
	}
	public LocalDate getDataInizioAffissioneA() {
		return dataInizioAffissioneA;
	}
	public void setDataInizioAffissioneA(LocalDate dataInizioAffissioneA) {
		this.dataInizioAffissioneA = dataInizioAffissioneA;
	}
	public LocalDate getDataFineAffissione() {
		return dataFineAffissione;
	}
	public void setDataFineAffissione(LocalDate dataFineAffissione) {
		this.dataFineAffissione = dataFineAffissione;
	}
	public LocalDate getDataFineAffissioneA() {
		return dataFineAffissioneA;
	}
	public void setDataFineAffissioneA(LocalDate dataFineAffissioneA) {
		this.dataFineAffissioneA = dataFineAffissioneA;
	}
	public String getRedigente() {
		return redigente;
	}
	public void setRedigente(String redigente) {
		this.redigente = redigente;
	}
	public LocalDate getDataRiunioneGiunta() {
		return dataRiunioneGiunta;
	}
	public void setDataRiunioneGiunta(LocalDate dataRiunioneGiunta) {
		this.dataRiunioneGiunta = dataRiunioneGiunta;
	}
	public LocalDate getDataRiunioneGiuntaA() {
		return dataRiunioneGiuntaA;
	}
	public void setDataRiunioneGiuntaA(LocalDate dataRiunioneGiuntaA) {
		this.dataRiunioneGiuntaA = dataRiunioneGiuntaA;
	}
	public String getOraRiunioneGiunta() {
		return oraRiunioneGiunta;
	}
	public void setOraRiunioneGiunta(String oraRiunioneGiunta) {
		this.oraRiunioneGiunta = oraRiunioneGiunta;
	}
	public String getAnnullato() {
		return annullato;
	}
	public void setAnnullato(String annullato) {
		this.annullato = annullato;
	}
	public String getTipoRiunioneGiunta() {
		return tipoRiunioneGiunta;
	}
	public void setTipoRiunioneGiunta(String tipoRiunioneGiunta) {
		this.tipoRiunioneGiunta = tipoRiunioneGiunta;
	}
	public String getNumRiunioneGiunta() {
		return numRiunioneGiunta;
	}
	public void setNumRiunioneGiunta(String numRiunioneGiunta) {
		this.numRiunioneGiunta = numRiunioneGiunta;
	}
	public String getRegolamento() {
		return regolamento;
	}
	public void setRegolamento(String regolamento) {
		this.regolamento = regolamento;
	}
	public String getAooDescr() {
		return aooDescr;
	}
	public void setAooDescr(String aooDescr) {
		this.aooDescr = aooDescr;
	}
	
}

/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.trasparenza.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.linksmt.assatti.cooperation.dto.AllegatoDto;

/**
 *
 * @author Gianluca Pindinelli
 *
 */
@JsonInclude(Include.NON_NULL)
public class AttoDto implements Serializable {

	private static final long serialVersionUID = -4715320766109875928L;
	private long id;
	private String numeroAdozione;
	private Date dataAdozione;
	private Date dataEsecutivita;
	private Date dataPubblicazione;
	private Date dataFinePubblicazione;
	private Date dataPresentazione;
	private Date dataApprovazione;
	private Date dataScadenza;
	private String ufficio;
	private String relatore;
	private String oggetto;
	private List<AllegatoDto> allegati;
	private TipoAttoDto tipoAttoDto;
	private String stato;
	private String esito;
	private String assessore;
	private String proponente;
	private Long attoId;
	private Boolean riservato;
	private Boolean scaduto;
	private String statoAnnullamento;
	private String descrizioneStatoAnnullamento;
	private String numeroAttoAnnullamento;
	private Date dataAttoAnnullamento;
	private String tipoAttoAnnullamento;
	private String tipoCollegamentoAtto;
	private Boolean visibilitaDocumenti;
	private Long attoRevocatoId;
	private List<ParereDto> pareri;
	private VotazioniDto votazioni;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the numeroAdozione
	 */
	public String getNumeroAdozione() {
		return numeroAdozione;
	}

	/**
	 * @param numeroAdozione the numeroAdozione to set
	 */
	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}

	/**
	 * @return the dataAdozione
	 */
	public Date getDataAdozione() {
		return dataAdozione;
	}

	/**
	 * @param dataAdozione the dataAdozione to set
	 */
	public void setDataAdozione(Date dataAdozione) {
		this.dataAdozione = dataAdozione;
	}

	/**
	 * @return the dataEsecutivita
	 */
	public Date getDataEsecutivita() {
		return dataEsecutivita;
	}

	/**
	 * @param dataEsecutivita the dataEsecutivita to set
	 */
	public void setDataEsecutivita(Date dataEsecutivita) {
		this.dataEsecutivita = dataEsecutivita;
	}

	/**
	 * @return the dataPubblicazione
	 */
	public Date getDataPubblicazione() {
		return dataPubblicazione;
	}

	/**
	 * @param dataPubblicazione the dataPubblicazione to set
	 */
	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	/**
	 * @return the dataPresentazione
	 */
	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	/**
	 * @param dataPresentazione the dataPresentazione to set
	 */
	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	/**
	 * @return the dataApprovazione
	 */
	public Date getDataApprovazione() {
		return dataApprovazione;
	}

	/**
	 * @param dataApprovazione the dataApprovazione to set
	 */
	public void setDataApprovazione(Date dataApprovazione) {
		this.dataApprovazione = dataApprovazione;
	}

	/**
	 * @return the dataScadenza
	 */
	public Date getDataScadenza() {
		return dataScadenza;
	}

	/**
	 * @param dataScadenza the dataScadenza to set
	 */
	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	/**
	 * @return the ufficio
	 */
	public String getUfficio() {
		return ufficio;
	}

	/**
	 * @param ufficio the ufficio to set
	 */
	public void setUfficio(String ufficio) {
		this.ufficio = ufficio;
	}

	/**
	 * @return the relatore
	 */
	public String getRelatore() {
		return relatore;
	}

	/**
	 * @param relatore the relatore to set
	 */
	public void setRelatore(String relatore) {
		this.relatore = relatore;
	}

	/**
	 * @return the oggetto
	 */
	public String getOggetto() {
		return oggetto;
	}

	/**
	 * @param oggetto the oggetto to set
	 */
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	/**
	 * @return the allegati
	 */
	public List<AllegatoDto> getAllegati() {
		return allegati;
	}

	/**
	 * @param allegati the allegati to set
	 */
	public void setAllegati(List<AllegatoDto> allegati) {
		this.allegati = allegati;
	}

	/**
	 * @return the tipoAttoDto
	 */
	public TipoAttoDto getTipoAttoDto() {
		return tipoAttoDto;
	}

	/**
	 * @param tipoAttoDto the tipoAttoDto to set
	 */
	public void setTipoAttoDto(TipoAttoDto tipoAttoDto) {
		this.tipoAttoDto = tipoAttoDto;
	}

	/**
	 * @return the stato
	 */
	public String getStato() {
		return stato;
	}

	/**
	 * @param stato the stato to set
	 */
	public void setStato(String stato) {
		this.stato = stato;
	}

	/**
	 * @return the esito
	 */
	public String getEsito() {
		return esito;
	}

	/**
	 * @param esito the esito to set
	 */
	public void setEsito(String esito) {
		this.esito = esito;
	}

	/**
	 * @return the assessore
	 */
	public String getAssessore() {
		return assessore;
	}

	/**
	 * @param assessore the assessore to set
	 */
	public void setAssessore(String assessore) {
		this.assessore = assessore;
	}

	/**
	 * @return the proponente
	 */
	public String getProponente() {
		return proponente;
	}

	/**
	 * @param proponente the proponente to set
	 */
	public void setProponente(String proponente) {
		this.proponente = proponente;
	}

	/**
	 * @return the attoId
	 */
	public Long getAttoId() {
		return attoId;
	}

	/**
	 * @param attoId the attoId to set
	 */
	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}

	/**
	 * @return the riservato
	 */
	public Boolean getRiservato() {
		return riservato;
	}

	/**
	 * @param riservato the riservato to set
	 */
	public void setRiservato(Boolean riservato) {
		this.riservato = riservato;
	}

	/**
	 * @return the statoAnnullamento
	 */
	public String getStatoAnnullamento() {
		return statoAnnullamento;
	}

	/**
	 * @param statoAnnullamento
	 */
	public void setStatoAnnullamento(String statoAnnullamento) {
		this.statoAnnullamento = statoAnnullamento;
	}
	
	public String getDescrizioneStatoAnnullamento() {
		return descrizioneStatoAnnullamento;
	}

	public void setDescrizioneStatoAnnullamento(String descrizioneStatoAnnullamento) {
		this.descrizioneStatoAnnullamento = descrizioneStatoAnnullamento;
	}

	/**
	 * @return the numeroAttoAnnullamento
	 */
	public String getNumeroAttoAnnullamento() {
		return numeroAttoAnnullamento;
	}

	/**
	 * @param numeroAttoAnnullamento
	 */
	public void setNumeroAttoAnnullamento(String numeroAttoAnnullamento) {
		this.numeroAttoAnnullamento = numeroAttoAnnullamento;
	}

	/**
	 * @return the dataAttoAnnullamento
	 */
	public Date getDataAttoAnnullamento() {
		return dataAttoAnnullamento;
	}

	/**
	 * @param dataAttoAnnullamento
	 */
	public void setDataAttoAnnullamento(Date dataAttoAnnullamento) {
		this.dataAttoAnnullamento = dataAttoAnnullamento;
	}

	/**
	 * @return the tipoAttoAnnullamento
	 */
	public String getTipoAttoAnnullamento() {
		return tipoAttoAnnullamento;
	}

	/**
	 * @param tipoAttoAnnullamento
	 */
	public void setTipoAttoAnnullamento(String tipoAttoAnnullamento) {
		this.tipoAttoAnnullamento = tipoAttoAnnullamento;
	}
	
	

	public String getTipoCollegamentoAtto() {
		return tipoCollegamentoAtto;
	}

	public void setTipoCollegamentoAtto(String tipoCollegamentoAtto) {
		this.tipoCollegamentoAtto = tipoCollegamentoAtto;
	}

	/**
	 * @return the attoRevocatoId
	 */
	public Long getAttoRevocatoId() {
		return attoRevocatoId;
	}

	/**
	 * @param attoRevocatoId
	 */
	public void setAttoRevocatoId(Long attoRevocatoId) {
		this.attoRevocatoId = attoRevocatoId;
	}

	/**
	 * @return the pareri
	 */
	public List<ParereDto> getPareri() {
		return pareri;
	}

	/**
	 * @param pareri
	 */
	public void setPareri(List<ParereDto> pareri) {
		this.pareri = pareri;
	}

	/**
	 * @return the votazioni
	 */
	public VotazioniDto getVotazioni() {
		return votazioni;
	}

	/**
	 * @param votazioni
	 */
	public void setVotazioni(VotazioniDto votazioni) {
		this.votazioni = votazioni;
	}

	public Date getDataFinePubblicazione() {
		return dataFinePubblicazione;
	}

	public void setDataFinePubblicazione(Date dataFinePubblicazione) {
		this.dataFinePubblicazione = dataFinePubblicazione;
	}

	public Boolean getScaduto() {
		return scaduto;
	}

	public void setScaduto(Boolean scaduto) {
		this.scaduto = scaduto;
	}

	public Boolean getVisibilitaDocumenti() {
		return visibilitaDocumenti;
	}

	public void setVisibilitaDocumenti(Boolean visibilitaDocumenti) {
		this.visibilitaDocumenti = visibilitaDocumenti;
	}
	
	
	
}
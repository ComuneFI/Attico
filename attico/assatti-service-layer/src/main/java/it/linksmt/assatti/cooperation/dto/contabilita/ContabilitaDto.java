/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Gianluca Pindinelli
 *
 */
public class ContabilitaDto implements Serializable {

	private static final long serialVersionUID = -8077068403240330533L;

	private String codiceTipoAtto;
	private Integer annoCreazioneProposta;
	private String numeroProposta;
	private Date dataCreazioneProposta;
	private BigDecimal importoTotale;
	private String oggetto;

	private String numeroAtto;
	private Integer annoAtto;
	private Date dataAdozioneAtto;
	private Date dataEsecutivita;
	private String responsabileProcedimento;

	@Override
	public String toString() {
		return "ContabilitaDto [codiceTipoAtto=" + codiceTipoAtto + ", annoCreazioneProposta=" + annoCreazioneProposta
				+ ", numeroProposta=" + numeroProposta + ", dataCreazioneProposta=" + dataCreazioneProposta
				+ ", importoTotale=" + importoTotale + ", oggetto=" + oggetto + ", numeroAtto=" + numeroAtto
				+ ", annoAtto=" + annoAtto + ", dataAdozioneAtto=" + dataAdozioneAtto + ", dataEsecutivita="
				+ dataEsecutivita + ", responsabileProcedimento=" + responsabileProcedimento + "]";
	}

	/**
	 * @return the codiceTipoAtto
	 */
	public String getCodiceTipoAtto() {
		return codiceTipoAtto;
	}

	/**
	 * @param codiceTipoAtto the codiceTipoAtto to set
	 */
	public void setCodiceTipoAtto(String codiceTipoAtto) {
		this.codiceTipoAtto = codiceTipoAtto;
	}

	/**
	 * @return the numeroProposta
	 */
	public String getNumeroProposta() {
		return numeroProposta;
	}

	/**
	 * @param numeroProposta the numeroProposta to set
	 */
	public void setNumeroProposta(String numeroProposta) {
		this.numeroProposta = numeroProposta;
	}

	/**
	 * @return the dataCreazioneProposta
	 */
	public Date getDataCreazioneProposta() {
		return dataCreazioneProposta;
	}

	/**
	 * @param dataCreazioneProposta the dataCreazioneProposta to set
	 */
	public void setDataCreazioneProposta(Date dataCreazioneProposta) {
		this.dataCreazioneProposta = dataCreazioneProposta;
	}

	/**
	 * @return the importoTotale
	 */
	public BigDecimal getImportoTotale() {
		return importoTotale;
	}

	/**
	 * @param importoTotale the importoTotale to set
	 */
	public void setImportoTotale(BigDecimal importoTotale) {
		this.importoTotale = importoTotale;
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
	 * @return the annoCreazioneProposta
	 */
	public Integer getAnnoCreazioneProposta() {
		return annoCreazioneProposta;
	}

	/**
	 * @param annoCreazioneProposta the annoCreazioneProposta to set
	 */
	public void setAnnoCreazioneProposta(Integer annoCreazioneProposta) {
		this.annoCreazioneProposta = annoCreazioneProposta;
	}

	/**
	 * @return the numeroAtto
	 */
	public String getNumeroAtto() {
		return numeroAtto;
	}

	/**
	 * @param numeroAtto the numeroAtto to set
	 */
	public void setNumeroAtto(String numeroAtto) {
		this.numeroAtto = numeroAtto;
	}

	/**
	 * @return the dataAdozioneAtto
	 */
	public Date getDataAdozioneAtto() {
		return dataAdozioneAtto;
	}

	/**
	 * @param dataAdozioneAtto the dataAdozioneAtto to set
	 */
	public void setDataAdozioneAtto(Date dataAdozioneAtto) {
		this.dataAdozioneAtto = dataAdozioneAtto;
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
	 * @return the responsabileProcedimento
	 */
	public String getResponsabileProcedimento() {
		return responsabileProcedimento;
	}

	/**
	 * @param responsabileProcedimento the responsabileProcedimento to set
	 */
	public void setResponsabileProcedimento(String responsabileProcedimento) {
		this.responsabileProcedimento = responsabileProcedimento;
	}

	/**
	 * @return the annoAtto
	 */
	public Integer getAnnoAtto() {
		return annoAtto;
	}

	/**
	 * @param annoAtto the annoAtto to set
	 */
	public void setAnnoAtto(Integer annoAtto) {
		this.annoAtto = annoAtto;
	}

}

/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.albo.dto;

import java.io.Serializable;

/**
 * @author Gianluca Pindinelli
 *
 */
public class AttoTxtDto implements Serializable {

	private static final long serialVersionUID = 4920978146602946221L;

	public static final int DATA_INIZIO_PUBBLICAZIONE_MAX_LENGHT = 8;
	public static final int DATA_FINE_PUBBLICAZIONE_MAX_LENGHT = 8;
	public static final int ENTE_RICHIEDENTE_MAX_LENGHT = 15;
	public static final int ENTE_EMITTENTE_MAX_LENGHT = 15;
	public static final int NUMERO_ATTO_MAX_LENGHT = 15;
	public static final int DATA_ATTO_MAX_LENGHT = 8;
	public static final int OGGETTO_MAX_LENGHT = 660;
	public static final int TIPO_ATTO_MAX_LENGHT = 15;

	private String dataInizioPubblicazione;
	private String dataFinePubblicazione;
	private String enteRichiedente;
	private String enteMittente;
	private String tipoAtto;
	private String dataAtto;
	private String numeroAtto;
	private String oggetto;

	/**
	 * @return the dataInizioPubblicazione
	 */
	public String getDataInizioPubblicazione() {
		return dataInizioPubblicazione;
	}

	/**
	 * @param dataInizioPubblicazione the dataInizioPubblicazione to set
	 */
	public void setDataInizioPubblicazione(String dataInizioPubblicazione) {
		this.dataInizioPubblicazione = dataInizioPubblicazione;
	}

	/**
	 * @return the dataFinePubblicazione
	 */
	public String getDataFinePubblicazione() {
		return dataFinePubblicazione;
	}

	/**
	 * @param dataFinePubblicazione the dataFinePubblicazione to set
	 */
	public void setDataFinePubblicazione(String dataFinePubblicazione) {
		this.dataFinePubblicazione = dataFinePubblicazione;
	}

	/**
	 * @return the enteRichiedente
	 */
	public String getEnteRichiedente() {
		return enteRichiedente;
	}

	/**
	 * @param enteRichiedente the enteRichiedente to set
	 */
	public void setEnteRichiedente(String enteRichiedente) {
		this.enteRichiedente = enteRichiedente;
	}

	/**
	 * @return the enteMittente
	 */
	public String getEnteMittente() {
		return enteMittente;
	}

	/**
	 * @param enteMittente the enteMittente to set
	 */
	public void setEnteMittente(String enteMittente) {
		this.enteMittente = enteMittente;
	}

	/**
	 * @return the tipoAtto
	 */
	public String getTipoAtto() {
		return tipoAtto;
	}

	/**
	 * @param tipoAtto the tipoAtto to set
	 */
	public void setTipoAtto(String tipoAtto) {
		this.tipoAtto = tipoAtto;
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
	 * @return the dataAtto
	 */
	public String getDataAtto() {
		return dataAtto;
	}

	/**
	 * @param dataAtto the dataAtto to set
	 */
	public void setDataAtto(String dataAtto) {
		this.dataAtto = dataAtto;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return dataInizioPubblicazione + enteRichiedente + enteMittente + tipoAtto + numeroAtto + dataAtto + oggetto + dataFinePubblicazione;
	}

}

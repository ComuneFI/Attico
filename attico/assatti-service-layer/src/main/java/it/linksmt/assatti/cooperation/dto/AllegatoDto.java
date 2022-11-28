/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 * @author Gianluca Pindinelli
 *
 */
@JsonInclude(Include.NON_NULL)
public class AllegatoDto implements Serializable {

	private static final long serialVersionUID = 7806943347041352858L;
	private long id;
	private String nome;
	private String titolo;
	private String descrizione;
	private String link;
	private String contentType;
	private boolean principale;
	private boolean riservato;
	private byte[] contenuto;

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
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the descrizione
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param descrizione the descrizione to set
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the principale
	 */
	public boolean isPrincipale() {
		return principale;
	}

	/**
	 * @param principale the principale to set
	 */
	public void setPrincipale(boolean principale) {
		this.principale = principale;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the contenuto
	 */
	public byte[] getContenuto() {
		return contenuto;
	}

	/**
	 * @param contenuto the contenuto to set
	 */
	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public boolean isRiservato() {
		return riservato;
	}

	public void setRiservato(boolean riservato) {
		this.riservato = riservato;
	}

}
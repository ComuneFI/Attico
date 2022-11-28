/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;


/**
 * @author Gianluca Pindinelli
 *
 */
public class MovimentoDocumentoDto implements Serializable {

	private static final long serialVersionUID = -740088730489121460L;

	private String id;
    private String soggettoCod;
    private String soggettoNome;
    private String anno;
    private String numero;
    private String tipo;
    private String data;
    private String oggetto;
    private String importo;
    private MovimentoImpegnoDto impegno;
    
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSoggettoCod() {
		return soggettoCod;
	}
	public void setSoggettoCod(String soggettoCod) {
		this.soggettoCod = soggettoCod;
	}
	public String getSoggettoNome() {
		return soggettoNome;
	}
	public void setSoggettoNome(String soggettoNome) {
		this.soggettoNome = soggettoNome;
	}
	public String getAnno() {
		return anno;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getImporto() {
		return importo;
	}
	public void setImporto(String importo) {
		this.importo = importo;
	}
	public MovimentoImpegnoDto getImpegno() {
		return impegno;
	}
	public void setImpegno(MovimentoImpegnoDto impegno) {
		this.impegno = impegno;
	}
}

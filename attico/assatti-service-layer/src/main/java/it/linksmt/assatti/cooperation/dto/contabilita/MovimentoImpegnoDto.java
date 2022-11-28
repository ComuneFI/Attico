package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;

public class MovimentoImpegnoDto implements Serializable {

	private static final long serialVersionUID = 3512322531823548907L;

	private String anno;
	private String numero;
	private String sub;
	private String oggetto;
	
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
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
}

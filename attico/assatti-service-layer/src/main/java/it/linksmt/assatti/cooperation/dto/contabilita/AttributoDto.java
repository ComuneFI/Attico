package it.linksmt.assatti.cooperation.dto.contabilita;

import java.io.Serializable;

public class AttributoDto implements Serializable {

	private static final long serialVersionUID = 3512322531823548907L;

	private String codice;
	private String descrizione;
	private String valoreCodice;
	private String valoreDescrizione;
	
	
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getValoreCodice() {
		return valoreCodice;
	}
	public void setValoreCodice(String valoreCodice) {
		this.valoreCodice = valoreCodice;
	}
	public String getValoreDescrizione() {
		return valoreDescrizione;
	}
	public void setValoreDescrizione(String valoreDescrizione) {
		this.valoreDescrizione = valoreDescrizione;
	}
}

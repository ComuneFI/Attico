package it.linksmt.assatti.datalayer.domain;

public enum VotazioneEnum {
	
	FAVOREVOLE("FAV", "Favorevole"),
	CONTRARIO ("CON", "Contrario"),
	ASTENUTO  ("AST", "Astenuto"),
	NON_VOTANTE  ("NPV", "Presente non votante");

	private String descrizione;
	private String codice;

	private VotazioneEnum(String codice, String descrizione) {
		this.codice = codice;
		this.descrizione = descrizione;
	}
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
}

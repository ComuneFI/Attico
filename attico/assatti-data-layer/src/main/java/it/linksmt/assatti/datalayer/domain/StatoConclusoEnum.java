package it.linksmt.assatti.datalayer.domain;

public enum StatoConclusoEnum {
	
	COMPLETO("COMPLETO"),
	RITIRATO("RITIRATO"),
	ANNULLATO("ANNULLATO"),
	RESPINTO("RESPINTO"),
	DECADUTO("DECADUTO"),
	ARCHIVIATO("ARCHIVIATO"),
	DATA_ESECUTIVITA("DATA_ESECUTIVITA"),
	ATTESA_RELATA("ATTESA_RELATA");

	private String codice;
	
	private StatoConclusoEnum(String codice) {
		this.codice = codice;
	}
	
	public String getCodice() {
		return codice;
	}
}

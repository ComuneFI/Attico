package it.linksmt.assatti.service.util;

public enum TipiAttivitaEnum {
	VISTA_E_INOLTRA("Vista e Inoltra");
	
	private String descrizione;
	
	private TipiAttivitaEnum(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
}

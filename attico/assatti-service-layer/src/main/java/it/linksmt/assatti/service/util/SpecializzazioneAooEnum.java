package it.linksmt.assatti.service.util;

public enum SpecializzazioneAooEnum {
	DIREZIONE_GENERALE("Direzione Generale");
	
	private String descrizione;
	
	private SpecializzazioneAooEnum(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
}

package it.linksmt.assatti.datalayer.domain;

public enum OrigineParereEnum {
	
	COMMISSIONE("C", "Commissione"),
	RISPOSTA("R","Risposta"),
	QUARTEREREVISORI("Q","Quartiere Revisori"),
	RELATORE("REL","Relatore");

	private String codice;
	private String descrizione;
	
	private OrigineParereEnum(String codice, String descrizione) {
		this.codice = codice;
		this.descrizione = descrizione;
	}
	
	public String getCodice() {
		return codice;
	}

	public String getDescrizione() {
		return descrizione;
	}
	
	public static String getDescrizione(String codice) {		
		for(OrigineParereEnum stato : OrigineParereEnum.values()) {
			if (stato.codice.equalsIgnoreCase(codice)) {
				return stato.descrizione;
			}
		}
		return null;
	}
}

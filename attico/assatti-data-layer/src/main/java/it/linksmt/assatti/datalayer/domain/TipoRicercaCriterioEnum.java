package it.linksmt.assatti.datalayer.domain;

public enum TipoRicercaCriterioEnum {
	
	INCLUSIVO("I", "Inclusivo"),
	ESCLUSIVO("E","Esclusivo");

	private String codice;
	private String descrizione;
	
	private TipoRicercaCriterioEnum(String codice, String descrizione) {
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
		for(TipoRicercaCriterioEnum stato : TipoRicercaCriterioEnum.values()) {
			if (stato.codice.equalsIgnoreCase(codice)) {
				return stato.descrizione;
			}
		}
		return null;
	}
}

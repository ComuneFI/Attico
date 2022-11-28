package it.linksmt.assatti.cooperation.trasparenza.dto;

public enum StatoAnnullamentoEnum {
	
	REVOCATO("R", "Revocato"),
	MODIFICATO("M", "Modificato-Integrato");

	private String codice;
	private String descrizione;
	
	private StatoAnnullamentoEnum(String codice, String descrizione) {
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
		for(StatoAnnullamentoEnum stato : StatoAnnullamentoEnum.values()) {
			if (stato.codice.equalsIgnoreCase(codice)) {
				return stato.descrizione;
			}
		}
		return null;
	}
}

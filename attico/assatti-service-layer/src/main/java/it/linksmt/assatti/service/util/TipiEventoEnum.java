package it.linksmt.assatti.service.util;

public enum TipiEventoEnum {
	FIRMA_RESP_TECNICO("FIRMA_RESP_TECNICO");
	
	private String codice;
	
	private TipiEventoEnum(String codice) {
		this.codice = codice;
	}
	
	public String getCodice() {
		return codice;
	}
}

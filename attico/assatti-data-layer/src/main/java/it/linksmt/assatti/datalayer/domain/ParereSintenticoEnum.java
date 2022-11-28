package it.linksmt.assatti.datalayer.domain;

public enum ParereSintenticoEnum {
	NON_ESPRESSO("non_espresso");
	
	private String codice;
	
	private ParereSintenticoEnum(String codice) {
		this.codice = codice;
	}
	
	public String getCodice() {
		return codice;
	}
}

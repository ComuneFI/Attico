package it.linksmt.assatti.datalayer.domain;

public enum TipoInvioNotificaEnum {
	SMS("SMS"),
	MAIL("E-Mail"),
	PEC("Posta Elettronica Certificata");
	
	private String descrizione;
	
	private TipoInvioNotificaEnum(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
}
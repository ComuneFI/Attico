package it.linksmt.assatti.service.util;

public enum TaskDesktopEnum {
	IN_CARICO("carico"),
	IN_ARRIVO("arrivo"),
	RIASSEGNAZIONE("attiRiassegnazione"),
	ATTUAZIONE_IN_SCADENZA("attuazioneInScadenza"),
	VISTO_MASSIVO("visto-massivo"),
	FIRMA_MASSIVA("firma-massiva"),
	LAVORATI("terminati"),
	MONITORAGGIO("monitoraggio"),
	MONITORAGGIO_GROUP("monitoraggioGroup"),
	CARICHI_ASSESSORI("attiInCaricoAssessori"),
	COORDINAMENTO_TESTO_CONSIGLIO("attiInCoordinamentoTesto-consiglio"),
	COORDINAMENTO_TESTO_GIUNTA("attiInCoordinamentoTesto-giunta"),
	POST_SEDUTA_CONSIGLIO("postSeduta-consiglio"),
	POST_SEDUTA_GIUNTA("postSeduta-giunta"),
	ATTI_IN_RAGIONERIA_PER_ARRIVO("attiInRagioneriaPerArrivo"),
	ATTI_IN_RAGIONERIA_PER_SCADENZA("attiInRagioneriaPerScadenza");
	
	private String descrizione;
	
	private TaskDesktopEnum(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
}

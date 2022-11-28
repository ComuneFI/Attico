package it.linksmt.assatti.datalayer.domain;

public enum TipoAzioneEnum {

	VISTO_ASSESSORE("visto_assessore","Visto Assessore"),
	VISTO_CONSIGLIERE("visto_consigliere","Visto Consigliere");

	private String codice;
	private String descrizione;
	
	private TipoAzioneEnum(String codice, String descrizione) {
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
		for(TipoAzioneEnum stato : TipoAzioneEnum.values()) {
			if (stato.codice.equalsIgnoreCase(codice)) {
				return stato.descrizione;
			}
		}
		return null;
	}
}

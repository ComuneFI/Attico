package it.linksmt.assatti.service.util;

public enum TipiParereEnum {
	VISTO_CONTABILE("visto_contabile_firmatario"),
	VISTO_TECNICO("visto_resp_tecnico"),
	VISTO_TECNICO_MODIFICHE("visto_resp_tecnico_modifiche"),
	VISTO_TECNICO_MODIFICHE_C("visto_resp_tecnico_modifiche_c"),
	PARERE_ASSESORE("VISTO_ASSESSORI"),
	PARERE_COMMISSIONE("parere_commissione"),
	PARERE_QUARTIERE_REVISORI("parere_quartiere_revisori"),
	PARERE_REGOLARITA_TECNICA("regolarita_tecnica"),
	PARERE_REGOLARITA_CONTABILE("regolarita_contabile"),
	PARERE_CONSIGLIERE("VISTO_CONSIGLIERI"),
	PARERE_TECNICO("parere_resp_tecnico"),
	PARERE_CONTABILE("parere_resp_contabile"),
	PARERE_TECNICO_REGOLARITA_MODIFICHE("parere_resp_tecnico_regolarita_modifiche"),
	PARERE_CONTABILE_REGOLARITA_MODIFICHE("parere_resp_contabile_regolarita_modifiche"),
	VISTO_RESP_ISTRUTTORIA("visto_resp_istruttoria");
	
	private String codice;
	
	private TipiParereEnum(String codice) {
		this.codice = codice;
	}
	
	public String getCodice() {
		return codice;
	}
}

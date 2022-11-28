package it.linksmt.assatti.datalayer.domain;

public enum StatoRichiestaHDEnum {
	RICHIESTA_CHIUSA("CHIUSA"),
	RICHIESTA_APERTA("APERTA"),
	RICHIESTA_SOSPESA("SOSPESA");
	
	private StatoRichiestaHDEnum(String statoDB){
		this.statoDB = statoDB;
	}
	
	private String statoDB;

	public String getStatoDB() {
		return statoDB;
	}
}

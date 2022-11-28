package it.linksmt.assatti.datalayer.domain;

public enum UfficioFilterEnum {
	
	NESSUN_FILTRO("all"),
	PROPRIO_UFFICIO("myown"),
	UFFICI_DIVERSI_DAL_PROPRIO("others"),
	NON_PREVISTO("notexpected");
	
	private String key;
	
	private UfficioFilterEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public static UfficioFilterEnum getByKey(String key) {
		UfficioFilterEnum filter = UfficioFilterEnum.NESSUN_FILTRO;
		if(key!=null) {
			for(UfficioFilterEnum v : UfficioFilterEnum.values()) {
				if(v.getKey().equalsIgnoreCase(key)) {
					filter = v;
					break;
				}
			}
		}
		return filter;
	}

}

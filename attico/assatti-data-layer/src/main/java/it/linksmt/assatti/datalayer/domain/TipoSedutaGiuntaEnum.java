package it.linksmt.assatti.datalayer.domain;

public enum TipoSedutaGiuntaEnum {
	ORDINARIA(1),
	STRAORDINARIA(2);
	
	private int id;
	
	static public String getDescrizioneById(Integer id){
		String ret = null;
		if(id!=null){
			for(TipoSedutaGiuntaEnum tipo : TipoSedutaGiuntaEnum.values()){
				if(id.equals(tipo.id)){
					ret = tipo.name();
					break;
				}
			}
		}
		return ret;
	}
	
	private TipoSedutaGiuntaEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

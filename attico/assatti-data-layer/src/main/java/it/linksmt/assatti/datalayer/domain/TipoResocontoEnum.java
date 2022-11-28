package it.linksmt.assatti.datalayer.domain;

public enum TipoResocontoEnum {

	DOCUMENTO_DEF_ESITO(0, TipoDocumentoEnum.documento_definitivo_esito),
	DOCUMENTO_DEF_ELENCO_VERBALI(1, TipoDocumentoEnum.documento_definitivo_elenco_verbali);
	
	private TipoResocontoEnum(int id, TipoDocumentoEnum tipoDocumento){
		this.id = id;
		this.tipoDocumento = tipoDocumento;
	}
	
	private int id;
	private TipoDocumentoEnum tipoDocumento;

	public int getId() {
		return id;
	}

	public TipoDocumentoEnum getTipoDocumento() {
		return tipoDocumento;
	}
	
	static public TipoResocontoEnum getById(int id){
		for(TipoResocontoEnum tipo : TipoResocontoEnum.values()){
			if(id == tipo.id){
				return tipo;
			}
		}
		return null;
	}
}

package it.linksmt.assatti.gestatti.web.rest.dto;


public class RuoloSearchDTO {
	private String id;
	private String codice;
	private String descrizione;
	private Boolean qualifica;
	private String tipo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Boolean getQualifica() {
		return qualifica;
	}
	public void setQualifica(Boolean qualifica) {
		this.qualifica = qualifica;
	}
	@Override
	public String toString() {
		return "RuoloSearchDTO [id=" + id + ", codice=" + codice
				+ ", descrizione=" + descrizione + ", qualifica=" + qualifica
				+ "]";
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}

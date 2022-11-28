package it.linksmt.assatti.gestatti.web.rest.dto;


public class EsitoPareriSearchDTO {
	private String id;
	private String codice;
	private String tipoAtto;
	private String tipo;
	private String valore;
	
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

	public String getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAtto(String tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}



	@Override
	public String toString() {
		return "EsitoPareriSearchDTO [id=" + id + ", valore=" + valore
				+ ", tipo=" + tipo + ", tipoAtto=" + tipoAtto
				+ "]";
	}
	
	
}

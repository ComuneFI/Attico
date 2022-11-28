package it.linksmt.assatti.service.dto;


public class IndirizzoSearchDTO {
	private String id;
	private String dug;
	private String toponimo;
	private String civico;
	private String cap;
	private String comune;
	private String provincia;
	private String stato;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDug() {
		return dug;
	}
	public void setDug(String dug) {
		this.dug = dug;
	}
	public String getCivico() {
		return civico;
	}
	public void setCivico(String civico) {
		this.civico = civico;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	@Override
	public String toString() {
		return "IndirizzoSearchDTO [id=" + id + ", dug=" + dug + ", toponimo="
				+ toponimo + ", civico=" + civico + ", cap=" + cap
				+ ", comune=" + comune + ", provincia=" + provincia
				+ ", stato=" + stato + "]";
	}
	public String getToponimo() {
		return toponimo;
	}
	public void setToponimo(String toponimo) {
		this.toponimo = toponimo;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	
	
	
}

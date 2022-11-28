package it.linksmt.assatti.gestatti.web.rest.dto;


public class GruppoRuoloSearchDTO {
	private String id;
	private String denominazione;
	private String aoo;
	private String ruolo;
	private Long aooId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	public String getAoo() {
		return aoo;
	}
	public void setAoo(String aoo) {
		this.aoo = aoo;
	}
	public String getRuolo() {
		return ruolo;
	}
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}
	public Long getAooId() {
		return aooId;
	}
	public void setAooId(Long aooId) {
		this.aooId = aooId;
	}
	
	@Override
	public String toString() {
		return "GruppoRuoloSearchDTO [id=" + id + ", denominazione="
				+ denominazione + ", aoo=" + aoo + ", ruolo=" + ruolo
				+ ", aooId=" + aooId + "]";
	}
	
}

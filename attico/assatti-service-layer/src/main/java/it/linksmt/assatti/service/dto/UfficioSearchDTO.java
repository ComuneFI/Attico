package it.linksmt.assatti.service.dto;

public class UfficioSearchDTO {
	private String id;
	private String codice;
	private String descrizione;
	private String email;
	private String pec;
	private String responsabile;
	private String aoo;
	private String stato;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public String getResponsabile() {
		return responsabile;
	}

	public void setResponsabile(String responsabile) {
		this.responsabile = responsabile;
	}

	public String getAoo() {
		return aoo;
	}

	public void setAoo(String aoo) {
		this.aoo = aoo;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	@Override
	public String toString() {
		return "UfficioSearchDTO [id=" + id + ", codice=" + codice
				+ ", descrizione=" + descrizione + ", email=" + email
				+ ", pec=" + pec + ", responsabile=" + responsabile + ", aoo="
				+ aoo + ", stato=" + stato + "]";
	}

}

package it.linksmt.assatti.service.dto;


public class ModelloCampoSearchDTO {
	private String tipoCampo;
	private String codice;
	private String titolo;
	private Long profilo;
	private Long profiloIdLoggato;
	private Long utenteid;
	private Long tipoAttoId;
	private Long aooIdReferente;
	private String aoo;
	
	public Long getUtenteid() {
		return utenteid;
	}
	public void setUtenteid(Long utenteid) {
		this.utenteid = utenteid;
	}
	public String getTipoCampo() {
		return tipoCampo;
	}
	public void setTipoCampo(String tipoCampo) {
		this.tipoCampo = tipoCampo;
	}
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public Long getProfilo() {
		return profilo;
	}
	public void setProfilo(Long profilo) {
		this.profilo = profilo;
	}
	public Long getProfiloIdLoggato() {
		return profiloIdLoggato;
	}
	public void setProfiloIdLoggato(Long profiloIdLoggato) {
		this.profiloIdLoggato = profiloIdLoggato;
	}
	public Long getTipoAttoId() {
		return tipoAttoId;
	}
	public void setTipoAttoId(Long tipoAttoId) {
		this.tipoAttoId = tipoAttoId;
	}
	public Long getAooIdReferente() {
		return aooIdReferente;
	}
	public void setAooIdReferente(Long aooIdReferente) {
		this.aooIdReferente = aooIdReferente;
	}
	public String getAoo() {
		return aoo;
	}
	public void setAoo(String aoo) {
		this.aoo = aoo;
	}
	
}

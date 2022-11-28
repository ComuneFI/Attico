package it.linksmt.assatti.service.dto;

import java.io.Serializable;

public class ProfiloCriteriaDTO implements Serializable {
	
	private Boolean hasProfilo=false;
	private Boolean attivo=false;
	private Boolean valido=false;
	
	private Long aooId;
	private String nome;
	private String cognome;
	private String userName;
	private String email;
	
	
	public Boolean getHasProfilo() {
		return hasProfilo;
	}


	public void setHasProfilo(Boolean hasProfilo) {
		this.hasProfilo = hasProfilo;
	}


	public Boolean getAttivo() {
		return attivo;
	}


	public void setAttivo(Boolean attivo) {
		this.attivo = attivo;
	}


	public Boolean getValido() {
		return valido;
	}


	public void setValido(Boolean valido) {
		this.valido = valido;
	}


	public Long getAooId() {
		return aooId;
	}


	public void setAooId(Long aooId) {
		this.aooId = aooId;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCognome() {
		return cognome;
	}


	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	@Override
	public String toString() {
		return "QUtenteCriteria [hasProfilo=" + hasProfilo + ", attivo="
				+ attivo + ", valido=" + valido + ", aooId=" + aooId
				+ ", nome=" + nome + ", cognome=" + cognome + ", userName="
				+ userName + ", email=" + email + "]";
	}
	
	
}

package it.linksmt.assatti.service.dto;


public class UtenteSearchDTO {
	private String id;
	private String cognome;
	private String nome;
	private String username;
	private String stato;
	private String aoo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	public String getAoo() {
		return aoo;
	}
	public void setAoo(String aoo) {
		this.aoo = aoo;
	}	
}

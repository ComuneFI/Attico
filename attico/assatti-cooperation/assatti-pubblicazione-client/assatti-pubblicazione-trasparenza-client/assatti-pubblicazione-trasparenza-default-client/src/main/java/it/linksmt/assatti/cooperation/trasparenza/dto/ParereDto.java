package it.linksmt.assatti.cooperation.trasparenza.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ParereDto implements Serializable {

	private static final long serialVersionUID = 6023575869942701465L;
	
	private long id;
	private String nome;
	private String testo;
	
	private Date dataInvio;
	private Date dataScadenza;
	private Date dataParere;
	private String origine;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTesto() {
		return testo;
	}
	public void setTesto(String testo) {
		this.testo = testo;
	}
	public Date getDataInvio() {
		return dataInvio;
	}
	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}
	public Date getDataScadenza() {
		return dataScadenza;
	}
	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}
	public Date getDataParere() {
		return dataParere;
	}
	public void setDataParere(Date dataParere) {
		this.dataParere = dataParere;
	}
	public String getOrigine() {
		return origine;
	}
	public void setOrigine(String origine) {
		this.origine = origine;
	}
}

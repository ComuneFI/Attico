package it.linksmt.assatti.cooperation.trasparenza.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class VotazioniDto {
	
	private Integer numPresenti;
	
	private Integer numAstenuti;
	private String nomiAstenuti;
	
	private Integer numNpv;
	private String nomiNpv;
	
	private Integer numContrari;
	private String nomiContrari;
	
	private Integer numFavorevoli;
	private String nomiFavorevoli;
	
	public Integer getNumAstenuti() {
		return numAstenuti;
	}
	public void setNumAstenuti(Integer numAstenuti) {
		this.numAstenuti = numAstenuti;
	}
	public String getNomiAstenuti() {
		return nomiAstenuti;
	}
	public void setNomiAstenuti(String nomiAstenuti) {
		this.nomiAstenuti = nomiAstenuti;
	}
	public Integer getNumNpv() {
		return numNpv;
	}
	public void setNumNpv(Integer numNpv) {
		this.numNpv = numNpv;
	}
	public String getNomiNpv() {
		return nomiNpv;
	}
	public void setNomiNpv(String nomiNpv) {
		this.nomiNpv = nomiNpv;
	}
	public Integer getNumContrari() {
		return numContrari;
	}
	public void setNumContrari(Integer numContrari) {
		this.numContrari = numContrari;
	}
	public String getNomiContrari() {
		return nomiContrari;
	}
	public void setNomiContrari(String nomiContrari) {
		this.nomiContrari = nomiContrari;
	}
	public Integer getNumFavorevoli() {
		return numFavorevoli;
	}
	public void setNumFavorevoli(Integer numFavorevoli) {
		this.numFavorevoli = numFavorevoli;
	}
	public String getNomiFavorevoli() {
		return nomiFavorevoli;
	}
	public void setNomiFavorevoli(String nomiFavorevoli) {
		this.nomiFavorevoli = nomiFavorevoli;
	}
	public Integer getNumPresenti() {
		return numPresenti;
	}
	public void setNumPresenti(Integer numPresenti) {
		this.numPresenti = numPresenti;
	}
}

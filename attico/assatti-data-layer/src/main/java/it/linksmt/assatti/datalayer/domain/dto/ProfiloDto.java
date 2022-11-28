package it.linksmt.assatti.datalayer.domain.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import it.linksmt.assatti.datalayer.domain.GruppoRuolo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.Validita;

public class ProfiloDto implements Serializable {

	private static final long serialVersionUID = -7832622786725414351L;
	private Long id;
	private String descrizione;
	private Validita validita = new Validita();
	private String delega;
	private Boolean predefinito;
	private Set<TipoAtto> tipiAtto = new HashSet<TipoAtto>();
	private Utente utente;
	private AooDto aoo;
	private GruppoRuolo grupporuolo;
   	private Set<QualificaProfessionale> hasQualifica = new HashSet<QualificaProfessionale>();
	
   	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Validita getValidita() {
		return validita;
	}
	public void setValidita(Validita validita) {
		this.validita = validita;
	}
	public String getDelega() {
		return delega;
	}
	public void setDelega(String delega) {
		this.delega = delega;
	}
	public Boolean getPredefinito() {
		return predefinito;
	}
	public void setPredefinito(Boolean predefinito) {
		this.predefinito = predefinito;
	}
	public Set<TipoAtto> getTipiAtto() {
		return tipiAtto;
	}
	public void setTipiAtto(Set<TipoAtto> tipiAtto) {
		this.tipiAtto = tipiAtto;
	}
	public Utente getUtente() {
		return utente;
	}
	public void setUtente(Utente utente) {
		this.utente = utente;
	}
	public AooDto getAoo() {
		return aoo;
	}
	public void setAoo(AooDto aoo) {
		this.aoo = aoo;
	}
	public GruppoRuolo getGrupporuolo() {
		return grupporuolo;
	}
	public void setGrupporuolo(GruppoRuolo grupporuolo) {
		this.grupporuolo = grupporuolo;
	}
	public Set<QualificaProfessionale> getHasQualifica() {
		return hasQualifica;
	}
	public void setHasQualifica(Set<QualificaProfessionale> hasQualifica) {
		this.hasQualifica = hasQualifica;
	}    

}

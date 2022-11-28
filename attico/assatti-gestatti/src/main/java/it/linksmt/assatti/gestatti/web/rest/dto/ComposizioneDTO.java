package it.linksmt.assatti.gestatti.web.rest.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;


public class ComposizioneDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DateTime dataCreazione;
	private String descrizione;
	private Long id;
	private String organo;
	private Boolean predefinita;
	private Integer version;
	private List<ProfiloComposizioneDTO> profiliComposizione = new ArrayList<ProfiloComposizioneDTO>();
	
	
	
	public DateTime getDataCreazione() {
		return dataCreazione;
	}
	public void setDataCreazione(DateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrgano() {
		return organo;
	}
	public void setOrgano(String organo) {
		this.organo = organo;
	}
	public Boolean getPredefinita() {
		return predefinita;
	}
	public void setPredefinita(Boolean predefinita) {
		this.predefinita = predefinita;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public List<ProfiloComposizioneDTO> getProfiliComposizione() {
		return profiliComposizione;
	}
	public void setProfiliComposizione(List<ProfiloComposizioneDTO> profiliComposizione) {
		this.profiliComposizione = profiliComposizione;
	}
	
}

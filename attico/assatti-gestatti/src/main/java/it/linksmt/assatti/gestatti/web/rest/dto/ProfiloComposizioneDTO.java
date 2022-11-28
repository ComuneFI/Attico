package it.linksmt.assatti.gestatti.web.rest.dto;

import java.io.Serializable;

import org.joda.time.LocalDate;

import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;


public class ProfiloComposizioneDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Profilo profilo;
	private Long idProfiloComposizione;
	private Integer ordine;
	private QualificaProfessionale qualificaProfessionale;
	private Boolean valido;
	
	public Profilo getProfilo() {
		return profilo;
	}
	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}
	
	public Long getIdProfiloComposizione() {
		return idProfiloComposizione;
	}
	public void setIdProfiloComposizione(Long idProfiloComposizione) {
		this.idProfiloComposizione = idProfiloComposizione;
	}
	public Integer getOrdine() {
		return ordine;
	}
	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}
	public QualificaProfessionale getQualificaProfessionale() {
		return qualificaProfessionale;
	}
	public void setQualificaProfessionale(QualificaProfessionale qualificaProfessionale) {
		this.qualificaProfessionale = qualificaProfessionale;
	}
	public Boolean getValido() {
		return valido;
	}
	public void setValido(Boolean valido) {
		this.valido = valido;
	}
	
	
	
	
	}

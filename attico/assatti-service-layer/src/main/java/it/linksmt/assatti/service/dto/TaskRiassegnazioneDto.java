package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Profilo;

public class TaskRiassegnazioneDto implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
	private String codiceAtto;
	private String oggettoAtto;
	private Long attoId;
	private String lavorazione;
	private String assignmentVar;
	private List<Profilo> profili;
	private Long profiloOrigine;
	private Long profiloNuovo;
	private Long qualificaNuova;
	private Long confIncaricoId;
	private String taskId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodiceAtto() {
		return codiceAtto;
	}
	public void setCodiceAtto(String codiceAtto) {
		this.codiceAtto = codiceAtto;
	}
	public String getOggettoAtto() {
		return oggettoAtto;
	}
	public void setOggettoAtto(String oggettoAtto) {
		this.oggettoAtto = oggettoAtto;
	}
	public Long getAttoId() {
		return attoId;
	}
	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}
	public String getLavorazione() {
		return lavorazione;
	}
	public void setLavorazione(String lavorazione) {
		this.lavorazione = lavorazione;
	}
	public String getAssignmentVar() {
		return assignmentVar;
	}
	public void setAssignmentVar(String assignmentVar) {
		this.assignmentVar = assignmentVar;
	}
	public List<Profilo> getProfili() {
		return profili;
	}
	public void setProfili(List<Profilo> profili) {
		this.profili = profili;
	}
	public Long getProfiloOrigine() {
		return profiloOrigine;
	}
	public void setProfiloOrigine(Long profiloOrigine) {
		this.profiloOrigine = profiloOrigine;
	}
	public Long getProfiloNuovo() {
		return profiloNuovo;
	}
	public void setProfiloNuovo(Long profiloNuovo) {
		this.profiloNuovo = profiloNuovo;
	}
	public Long getQualificaNuova() {
		return qualificaNuova;
	}
	public void setQualificaNuova(Long qualificaNuova) {
		this.qualificaNuova = qualificaNuova;
	}
	public Long getConfIncaricoId() {
		return confIncaricoId;
	}
	public void setConfIncaricoId(Long confIncaricoId) {
		this.confIncaricoId = confIncaricoId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignmentVar == null) ? 0 : assignmentVar.hashCode());
		result = prime * result + ((attoId == null) ? 0 : attoId.hashCode());
		result = prime * result + ((codiceAtto == null) ? 0 : codiceAtto.hashCode());
		result = prime * result + ((confIncaricoId == null) ? 0 : confIncaricoId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lavorazione == null) ? 0 : lavorazione.hashCode());
		result = prime * result + ((oggettoAtto == null) ? 0 : oggettoAtto.hashCode());
		result = prime * result + ((profili == null) ? 0 : profili.hashCode());
		result = prime * result + ((profiloNuovo == null) ? 0 : profiloNuovo.hashCode());
		result = prime * result + ((profiloOrigine == null) ? 0 : profiloOrigine.hashCode());
		result = prime * result + ((qualificaNuova == null) ? 0 : qualificaNuova.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskRiassegnazioneDto other = (TaskRiassegnazioneDto) obj;
		if (assignmentVar == null) {
			if (other.assignmentVar != null)
				return false;
		} else if (!assignmentVar.equals(other.assignmentVar))
			return false;
		if (attoId == null) {
			if (other.attoId != null)
				return false;
		} else if (!attoId.equals(other.attoId))
			return false;
		if (codiceAtto == null) {
			if (other.codiceAtto != null)
				return false;
		} else if (!codiceAtto.equals(other.codiceAtto))
			return false;
		if (confIncaricoId == null) {
			if (other.confIncaricoId != null)
				return false;
		} else if (!confIncaricoId.equals(other.confIncaricoId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lavorazione == null) {
			if (other.lavorazione != null)
				return false;
		} else if (!lavorazione.equals(other.lavorazione))
			return false;
		if (oggettoAtto == null) {
			if (other.oggettoAtto != null)
				return false;
		} else if (!oggettoAtto.equals(other.oggettoAtto))
			return false;
		if (profili == null) {
			if (other.profili != null)
				return false;
		} else if (!profili.equals(other.profili))
			return false;
		if (profiloNuovo == null) {
			if (other.profiloNuovo != null)
				return false;
		} else if (!profiloNuovo.equals(other.profiloNuovo))
			return false;
		if (profiloOrigine == null) {
			if (other.profiloOrigine != null)
				return false;
		} else if (!profiloOrigine.equals(other.profiloOrigine))
			return false;
		if (qualificaNuova == null) {
			if (other.qualificaNuova != null)
				return false;
		} else if (!qualificaNuova.equals(other.qualificaNuova))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TaskRiassegnazioneDto [id=" + id + ", codiceAtto=" + codiceAtto + ", oggettoAtto=" + oggettoAtto
				+ ", attoId=" + attoId + ", lavorazione=" + lavorazione + ", assignmentVar=" + assignmentVar
				+ ", profili=" + profili + ", profiloOrigine=" + profiloOrigine + ", profiloNuovo=" + profiloNuovo
				+ ", qualificaNuova=" + qualificaNuova + ", confIncaricoId=" + confIncaricoId + ", taskId=" + taskId
				+ "]";
	}
}

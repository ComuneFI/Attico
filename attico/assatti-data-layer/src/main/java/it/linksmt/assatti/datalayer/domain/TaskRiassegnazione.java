package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TaskRiassegnazione.
 */
@Entity
@Table(name = "task_riassegnazione")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TaskRiassegnazione extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "atto_id")
	private Long attoId;
	
	@Column(name = "conf_incarico_id")
	private Long confIncaricoId;
	
	@Column(name = "profilo_origine")
	private Long profiloOrigine;
	
	@Column(name = "profilo_nuovo")
	private Long profiloNuovo;
	
	@Column(name = "qualifica_origine")
	private Long qualificaOrigine;
	
	@Column(name = "qualifica_nuova")
	private Long qualificaNuova;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAttoId() {
		return attoId;
	}

	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}

	public Long getConfIncaricoId() {
		return confIncaricoId;
	}

	public void setConfIncaricoId(Long confIncaricoId) {
		this.confIncaricoId = confIncaricoId;
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

	public Long getQualificaOrigine() {
		return qualificaOrigine;
	}

	public void setQualificaOrigine(Long qualificaOrigine) {
		this.qualificaOrigine = qualificaOrigine;
	}

	public Long getQualificaNuova() {
		return qualificaNuova;
	}

	public void setQualificaNuova(Long qualificaNuova) {
		this.qualificaNuova = qualificaNuova;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attoId == null) ? 0 : attoId.hashCode());
		result = prime * result + ((confIncaricoId == null) ? 0 : confIncaricoId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((profiloNuovo == null) ? 0 : profiloNuovo.hashCode());
		result = prime * result + ((profiloOrigine == null) ? 0 : profiloOrigine.hashCode());
		result = prime * result + ((qualificaNuova == null) ? 0 : qualificaNuova.hashCode());
		result = prime * result + ((qualificaOrigine == null) ? 0 : qualificaOrigine.hashCode());
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
		TaskRiassegnazione other = (TaskRiassegnazione) obj;
		if (attoId == null) {
			if (other.attoId != null)
				return false;
		} else if (!attoId.equals(other.attoId))
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
		if (qualificaOrigine == null) {
			if (other.qualificaOrigine != null)
				return false;
		} else if (!qualificaOrigine.equals(other.qualificaOrigine))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaskRiassegnazione [id=" + id + ", attoId=" + attoId + ", confIncaricoId=" + confIncaricoId
				+ ", profiloOrigine=" + profiloOrigine + ", profiloNuovo=" + profiloNuovo + ", qualificaOrigine="
				+ qualificaOrigine + ", qualificaNuova=" + qualificaNuova + "]";
	}
}
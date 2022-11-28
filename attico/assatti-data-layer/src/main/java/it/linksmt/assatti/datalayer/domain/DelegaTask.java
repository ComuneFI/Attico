package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DelegaTask.
 */
@Entity
@Table(name = "delegatask")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DelegaTask extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="tipo")
	private TipoDelegaTaskEnum tipo;

	@Column(name = "enabled")
    private Boolean enabled;

	@ManyToOne
	@JoinColumn(name="profilo_delegante_id", insertable = true, updatable = false, nullable = false)
	private Profilo profiloDelegante;
	
	@ManyToOne
	@JoinColumn(name="profilo_delegato_id", insertable = true, updatable = true, nullable = false)
	private Profilo profiloDelegato;
	
	@Column(name = "task_id", nullable = false, unique = true)
    private String taskBpmId;
	
	@Column(name = "assignee_originario", insertable = true, updatable = false, nullable = false)
    private String assigneeOriginario;
	
	@Column(name = "assignee_delegato", insertable = true, updatable = true, nullable = false)
    private String assigneeDelegato;
	
	@ManyToOne
	@JoinColumn(name="atto_id", insertable = true, updatable = false, nullable = false)
	private Atto atto;
	
	@Column(name = "lavorazione")
    private String lavorazione;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Profilo getProfiloDelegante() {
		return profiloDelegante;
	}

	public void setProfiloDelegante(Profilo profiloDelegante) {
		this.profiloDelegante = profiloDelegante;
	}

	public Profilo getProfiloDelegato() {
		return profiloDelegato;
	}

	public void setProfiloDelegato(Profilo profiloDelegato) {
		this.profiloDelegato = profiloDelegato;
	}

	public String getTaskBpmId() {
		return taskBpmId;
	}

	public void setTaskBpmId(String taskBpmId) {
		this.taskBpmId = taskBpmId;
	}

	public String getAssigneeOriginario() {
		return assigneeOriginario;
	}

	public void setAssigneeOriginario(String assigneeOriginario) {
		this.assigneeOriginario = assigneeOriginario;
	}

	public String getAssigneeDelegato() {
		return assigneeDelegato;
	}

	public void setAssigneeDelegato(String assigneeDelegato) {
		this.assigneeDelegato = assigneeDelegato;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public String getLavorazione() {
		return lavorazione;
	}

	public void setLavorazione(String lavorazione) {
		this.lavorazione = lavorazione;
	}

	public TipoDelegaTaskEnum getTipo() {
		return tipo;
	}

	public void setTipo(TipoDelegaTaskEnum tipo) {
		this.tipo = tipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assigneeDelegato == null) ? 0 : assigneeDelegato.hashCode());
		result = prime * result + ((assigneeOriginario == null) ? 0 : assigneeOriginario.hashCode());
		result = prime * result + ((atto == null) ? 0 : atto.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lavorazione == null) ? 0 : lavorazione.hashCode());
		result = prime * result + ((profiloDelegante == null) ? 0 : profiloDelegante.hashCode());
		result = prime * result + ((profiloDelegato == null) ? 0 : profiloDelegato.hashCode());
		result = prime * result + ((taskBpmId == null) ? 0 : taskBpmId.hashCode());
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
		DelegaTask other = (DelegaTask) obj;
		if (assigneeDelegato == null) {
			if (other.assigneeDelegato != null)
				return false;
		} else if (!assigneeDelegato.equals(other.assigneeDelegato))
			return false;
		if (assigneeOriginario == null) {
			if (other.assigneeOriginario != null)
				return false;
		} else if (!assigneeOriginario.equals(other.assigneeOriginario))
			return false;
		if (atto == null) {
			if (other.atto != null)
				return false;
		} else if (!atto.equals(other.atto))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
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
		if (profiloDelegante == null) {
			if (other.profiloDelegante != null)
				return false;
		} else if (!profiloDelegante.equals(other.profiloDelegante))
			return false;
		if (profiloDelegato == null) {
			if (other.profiloDelegato != null)
				return false;
		} else if (!profiloDelegato.equals(other.profiloDelegato))
			return false;
		if (taskBpmId == null) {
			if (other.taskBpmId != null)
				return false;
		} else if (!taskBpmId.equals(other.taskBpmId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DelegaTask [id=" + id + ", taskBpmId=" + taskBpmId + ", assigneeOriginario=" + assigneeOriginario
				+ ", assigneeDelegato=" + assigneeDelegato + ", lavorazione=" + lavorazione + "]";
	}
	
}

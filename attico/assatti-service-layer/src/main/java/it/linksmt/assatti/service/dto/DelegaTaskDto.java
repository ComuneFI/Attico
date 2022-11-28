package it.linksmt.assatti.service.dto;

import java.io.Serializable;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Profilo;

public class DelegaTaskDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
    private Boolean enabled;

	private Profilo profiloDelegante;
	
	private Profilo profiloDelegato;
	
    private String taskBpmId;
	
    private String lavorazione;
    
    private String codiceCifra;
    
    private String assigneeOriginario;
	
    private String assigneeDelegato;
	
	private Atto atto;
	
	private String tipo;
	
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

	public String getLavorazione() {
		return lavorazione;
	}

	public void setLavorazione(String lavorazione) {
		this.lavorazione = lavorazione;
	}

	public String getCodiceCifra() {
		return codiceCifra;
	}

	public void setCodiceCifra(String codiceCifra) {
		this.codiceCifra = codiceCifra;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiceCifra == null) ? 0 : codiceCifra.hashCode());
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
		DelegaTaskDto other = (DelegaTaskDto) obj;
		if (codiceCifra == null) {
			if (other.codiceCifra != null)
				return false;
		} else if (!codiceCifra.equals(other.codiceCifra))
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
		return "DelegaTaskDto [id=" + id + ", enabled=" + enabled + ", profiloDelegante=" + profiloDelegante
				+ ", profiloDelegato=" + profiloDelegato + ", taskBpmId=" + taskBpmId + ", lavorazione=" + lavorazione
				+ ", codiceCifra=" + codiceCifra + "]";
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
    
}

package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AttoSchedaDatoId implements Serializable{
	
	public AttoSchedaDatoId() {
	}
	
	public AttoSchedaDatoId(Long attoId, Long schedaId, Integer progressivoElemento, Long schedaDatoId) {
		super();
		this.attoId = attoId;
		this.schedaId = schedaId;
		this.progressivoElemento = progressivoElemento;
		this.schedaDatoId = schedaDatoId;
	}

	@Column(name = "atto_id")
	private Long attoId;
	
	@Column(name = "scheda_id")
	private Long schedaId;

	@Column(name = "progressivo_elemento", nullable=false)
    private Integer progressivoElemento;

	
	@Column(name = "scheda_dato_id")
	private Long schedaDatoId;
	
	public Long getSchedaId() {
		return schedaId;
	}


	public void setSchedaId(Long schedaId) {
		this.schedaId = schedaId;
	}


	public Long getAttoId() {
		return attoId;
	}


	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}


	public Long getSchedaDatoId() {
		return schedaDatoId;
	}


	public void setSchedaDatoId(Long schedaDatoId) {
		this.schedaDatoId = schedaDatoId;
	}


	public Integer getProgressivoElemento() {
		return progressivoElemento;
	}


	public void setProgressivoElemento(Integer progressivoElemento) {
		this.progressivoElemento = progressivoElemento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attoId == null) ? 0 : attoId.hashCode());
		result = prime * result + ((progressivoElemento == null) ? 0 : progressivoElemento.hashCode());
		result = prime * result + ((schedaDatoId == null) ? 0 : schedaDatoId.hashCode());
		result = prime * result + ((schedaId == null) ? 0 : schedaId.hashCode());
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
		AttoSchedaDatoId other = (AttoSchedaDatoId) obj;
		if (attoId == null) {
			if (other.attoId != null)
				return false;
		} else if (!attoId.equals(other.attoId))
			return false;
		if (progressivoElemento == null) {
			if (other.progressivoElemento != null)
				return false;
		} else if (!progressivoElemento.equals(other.progressivoElemento))
			return false;
		if (schedaDatoId == null) {
			if (other.schedaDatoId != null)
				return false;
		} else if (!schedaDatoId.equals(other.schedaDatoId))
			return false;
		if (schedaId == null) {
			if (other.schedaId != null)
				return false;
		} else if (!schedaId.equals(other.schedaId))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "AttoSchedaDatoId [attoId=" + attoId + ", schedaDatoId=" + schedaDatoId + ", progressivoElemento="
				+ progressivoElemento + "]";
	}
	
	
}

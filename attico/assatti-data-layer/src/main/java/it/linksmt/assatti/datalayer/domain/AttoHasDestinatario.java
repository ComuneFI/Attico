package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A AttoHasDestinatario.
 */
@Entity
@Table(name = "ATTO_HAS_DESTINATARIO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AttoHasDestinatario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8906223047533602249L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name="atto_id")
	private Long attoId;
	
	@Column(name="seduta_id")
	private Long sedutaId;
	
	@Column(name="documento_id")
	private Long documentoPdfId;
	
	public Long getAttoId() {
		return attoId;
	}

	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="tipodestinatario_id",insertable=true,updatable=false )
	private TipoDestinatario tipoDestinatario;
	
	@Column(name="destinatario_id")
	private Long destinatarioId;

	@Transient
	@JsonProperty
	private IDestinatarioInterno destinatario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoDestinatario getTipoDestinatario() {
		return tipoDestinatario;
	}

	public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
		this.tipoDestinatario = tipoDestinatario;
	}

	public Long getDestinatarioId() {
		return destinatarioId;
	}

	public void setDestinatarioId(Long destinatarioId) {
		this.destinatarioId = destinatarioId;
	}

	public IDestinatarioInterno getDestinatario() {
		return destinatario;
	}
	
	public Long getDocumentoPdfId() {
		return documentoPdfId;
	}

	public void setDocumentoPdfId(Long documentoPdfId) {
		this.documentoPdfId = documentoPdfId;
	}
	
	public Long getSedutaId() {
		return sedutaId;
	}

	public void setSedutaId(Long sedutaId) {
		this.sedutaId = sedutaId;
	}

	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
	public void setDestinatario(IDestinatarioInterno destinatario) {
		this.destinatario = destinatario;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttoHasDestinatario other = (AttoHasDestinatario) obj;
		if (attoId == null) {
			if (other.attoId != null)
				return false;
		} else if (!attoId.equals(other.attoId))
			return false;
		if (sedutaId == null) {
			if (other.sedutaId != null)
				return false;
		} else if (!sedutaId.equals(other.sedutaId))
			return false;
		if (documentoPdfId == null) {
			if (other.documentoPdfId != null)
				return false;
		} else if (!documentoPdfId.equals(other.documentoPdfId))
			return false;
		if (destinatarioId == null) {
			if (other.destinatarioId != null)
				return false;
		} else if (!destinatarioId.equals(other.destinatarioId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (tipoDestinatario == null) {
			if (other.tipoDestinatario != null)
				return false;
		} else if (!tipoDestinatario.equals(other.tipoDestinatario))
			return false;
		return true;
	}
	
	
}

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

@Entity
@Table(name = "ricerca_criterio")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CriterioRicerca implements Serializable {

	private static final long serialVersionUID = 1L;

	public CriterioRicerca() {
	}
	public CriterioRicerca( final Long id ) {
		this.id=id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "tipo", length=1)
	private String tipo;

	@Column(name = "codice")
	private String codice;

	@Column(name = "ordine")
	private Integer ordine;
	
	@Column(name = "aoo_based")
	private Boolean aooBased;
	
	@Column(name = "profilo_based")
	private Boolean profiloBased;

	public Boolean getAooBased() {
		return aooBased;
	}
	public void setAooBased(Boolean aooBased) {
		this.aooBased = aooBased;
	}
	public Boolean getProfiloBased() {
		return profiloBased;
	}
	public void setProfiloBased(Boolean profiloBased) {
		this.profiloBased = profiloBased;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public Integer getOrdine() {
		return ordine;
	}
	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codice == null) ? 0 : codice.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ordine == null) ? 0 : ordine.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
		CriterioRicerca other = (CriterioRicerca) obj;
		if (codice == null) {
			if (other.codice != null)
				return false;
		} else if (!codice.equals(other.codice))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ordine == null) {
			if (other.ordine != null)
				return false;
		} else if (!ordine.equals(other.ordine))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "RicercaCriterio [id=" + id + ", tipo=" + tipo + ", codice=" + codice + ", ordine=" + ordine + "]";
	}
}

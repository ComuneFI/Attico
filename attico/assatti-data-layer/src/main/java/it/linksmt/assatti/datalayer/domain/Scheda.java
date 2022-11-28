package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Scheda.
 */
@Entity
@Table(name = "SCHEDA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Scheda implements Serializable {
	
	public Scheda() {
	}
	
	public Scheda(Long id) {
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "etichetta")
	private String etichetta;

	@Column(name = "ripetitiva")
	private Boolean ripetitiva;

	@Column(name = "ordine")
	private Integer ordine;

	@OneToMany(mappedBy = "scheda")
	@OrderBy(value="ordine ASC")
	private Set<SchedaDato> campi = new HashSet<SchedaDato>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEtichetta() {
		return etichetta;
	}

	public void setEtichetta(String etichetta) {
		this.etichetta = etichetta;
	}

	public Boolean getRipetitiva() {
		return ripetitiva;
	}

	public void setRipetitiva(Boolean ripetitiva) {
		this.ripetitiva = ripetitiva;
	}

	public Integer getOrdine() {
		return ordine;
	}

	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}

	public Set<SchedaDato> getCampi() {
		return campi;
	}

	public void setCampi(Set<SchedaDato> campi) {
		this.campi = campi;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Scheda scheda = (Scheda) o;

		if (!Objects.equals(id, scheda.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Scheda{" + "id=" + id + ", etichetta='" + etichetta + "'" + ", ripetitiva='" + ripetitiva + "'"
				+ ", ordine='" + ordine + "'" + '}';
	}
}

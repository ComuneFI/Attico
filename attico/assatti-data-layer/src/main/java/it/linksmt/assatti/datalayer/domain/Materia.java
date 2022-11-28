package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Materia.
 */
@Entity
@Table(name = "MATERIA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Materia implements Serializable {

	public Materia() {
	}

	public Materia(Long id) {
		super();
		this.id = id;
	}

	public Materia(Long id, String descrizione) {
		super();
		this.id = id;
		this.descrizione = descrizione;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "descrizione")
	private String descrizione;

	@ManyToOne
	private TipoMateria tipoMateria;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aoo_id",insertable=true, updatable = true)
	private Aoo aoo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "materia")
	private Set<SottoMateria> sottoMaterie = new HashSet<SottoMateria>(0);

	@Embedded
	private Validita validita;

	@Transient
	@JsonSerialize
	public String dataType = "materia";
	
	@Transient
	@JsonProperty
	private Boolean atti;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public TipoMateria getTipoMateria() {
		return tipoMateria;
	}

	public void setTipoMateria(TipoMateria tipoMateria) {
		this.tipoMateria = tipoMateria;
	}

	public Set<SottoMateria> getSottoMaterie() {
		return sottoMaterie;
	}

	public void setSottoMaterie(Set<SottoMateria> sottoMaterie) {
		this.sottoMaterie = sottoMaterie;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}
	
	public Boolean getAtti() {
		return atti;
	}

	public void setAtti(Boolean atti) {
		this.atti = atti;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Materia materia = (Materia) o;

		if (!Objects.equals(id, materia.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Materia{" + "id=" + id + ", descrizione='" + descrizione + "'"
				+ '}';
	}
}

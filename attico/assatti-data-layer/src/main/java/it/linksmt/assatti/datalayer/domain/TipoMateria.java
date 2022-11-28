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
 * A TipoMateria.
 */
@Entity
@Table(name = "TIPOMATERIA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoMateria implements Serializable {

	public TipoMateria(Long idMateria) {
		super();
		this.id = idMateria;
	}

	public TipoMateria(Long idMateria, String descrizione) {
		super();
		this.id = idMateria;
		this.descrizione = descrizione;
	}

	public TipoMateria() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "descrizione")
	private String descrizione;

	@ManyToOne
	@JoinColumn(name="aoo_id",insertable=true,updatable=true)
	private Aoo aoo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoMateria")
	private Set<Materia> materie = new HashSet<Materia>(0);

	@Embedded
	private Validita validita = new Validita();

	@Transient
	@JsonSerialize
	public String dataType = "tipoMateria";
	
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

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public Set<Materia> getMaterie() {
		return materie;
	}

	public void setMaterie(Set<Materia> materie) {
		this.materie = materie;
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

		TipoMateria tipoMateria = (TipoMateria) o;

		if (!Objects.equals(id, tipoMateria.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TipoMateria{" + "id=" + id + ", descrizione='" + descrizione
				+ "'" + '}';
	}
}

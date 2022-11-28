package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A SottoMateria.
 */
@Entity
@Table(name = "SOTTOMATERIA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SottoMateria implements Serializable {
	public SottoMateria() {
	}

	public SottoMateria(Long id, String descrizione) {
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
	private Materia materia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="aoo_id",insertable=true,updatable=true)
	private Aoo aoo;

	@Embedded
	private Validita validita;

	@Transient
	@JsonSerialize
	public String dataType = "sottoMateria";
	
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

	public Materia getMateria() {
		return materia;
	}

	public void setMateria(Materia materia) {
		this.materia = materia;
	}

	public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
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

		SottoMateria sottoMateria = (SottoMateria) o;

		if (!Objects.equals(id, sottoMateria.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "SottoMateria{" + "id=" + id + ", descrizione='" + descrizione
				+ "'" + '}';
	}
}

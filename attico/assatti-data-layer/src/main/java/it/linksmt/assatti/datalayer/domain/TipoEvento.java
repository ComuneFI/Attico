package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TipoEvento.
 */
@Entity
@Table(name = "TIPOEVENTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoEvento implements Serializable {

	public TipoEvento() {
	}
	public TipoEvento( final Long id ) {
		this.id=id;
	}

	public TipoEvento(final Long id, final String codice, final String descrizione) {
		this.id=id;
		this.codice=codice;
		this.descrizione=descrizione;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "codice")
	private String codice;

	@Column(name = "descrizione")
	private String descrizione;

	@ManyToOne
	@JoinColumn(name = "categoriaEvento_id")
	private CategoriaEvento categoriaEvento;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(final String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(final String descrizione) {
		this.descrizione = descrizione;
	}

	public CategoriaEvento getCategoriaEvento() {
		return categoriaEvento;
	}
	
	public void setCategoriaEvento(CategoriaEvento categoriaEvento) {
		this.categoriaEvento = categoriaEvento;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TipoEvento tipoAtto = (TipoEvento) o;

		if (!Objects.equals(id, tipoAtto.id)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TipoEvento{" + "id=" + id + ", codice='" + codice + "'"
				+ ", descrizione='" + descrizione + "'" + '}';
	}
}

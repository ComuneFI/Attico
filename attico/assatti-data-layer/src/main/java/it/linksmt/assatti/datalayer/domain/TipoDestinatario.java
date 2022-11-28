package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TipoDestinatario.
 */
@Entity
@Table(name = "TIPODESTINATARIO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoDestinatario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4174477889700366962L;

	public TipoDestinatario() {
	}
	public TipoDestinatario( final Long id ) {
		this.id=id;
	}

	public TipoDestinatario(final Long id, final String descrizione) {
		this.id=id;
		this.descrizione=descrizione;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "descrizione")
	private String descrizione;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(final String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TipoDestinatario tipoAtto = (TipoDestinatario) o;

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
		return "TipoAtto{" + "id=" + id
				+ ", descrizione='" + descrizione + "'" + '}';
	}
}

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
 * A CategoriaEvento.
 */
@Entity
@Table(name = "CATEGORIAEVENTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CategoriaEvento implements Serializable {

	public CategoriaEvento() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "denominazione")
	private String denominazione;

	@Column(name = "ordine")
	private int ordine;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	public int getOrdine() {
		return ordine;
	}
	public void setOrdine(int ordine) {
		this.ordine = ordine;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CategoriaEvento tipoAtto = (CategoriaEvento) o;

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
		return "TipoEvento{" + "id=" + id + ", denominazione='" + denominazione + "'"
				+ ", ordine='" + ordine + "'" + '}';
	}
}

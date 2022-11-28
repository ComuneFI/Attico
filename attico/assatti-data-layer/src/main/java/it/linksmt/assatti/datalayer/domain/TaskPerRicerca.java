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
 * A TaskPerRicerca.
 */
@Entity
@Table(name = "TASK_PER_RICERCA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TaskPerRicerca implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4409076368589665739L;



	public TaskPerRicerca() {
	}
	public TaskPerRicerca( final Long id ) {
		this.id=id;
	}

	public TaskPerRicerca(final Long id, final String keycode, final String descrizione) {
		this.id=id;
		this.keycode=keycode;
		this.descrizione=descrizione;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="tipoatto_id", insertable = false, updatable = false)
	private TipoAtto tipoAtto;

	@Column(name = "key_code")
	private String keycode;

	@Column(name = "descrizione")
	private String descrizione;
	
	@Column(name = "type_code")
	private String typecode;
	
	

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
	
	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}
	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}
	public String getKeycode() {
		return keycode;
	}
	public void setKeycode(String keycode) {
		this.keycode = keycode;
	}
	public String getTypecode() {
		return typecode;
	}
	public void setTypecode(String typecode) {
		this.typecode = typecode;
	}
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TaskPerRicerca tipoAtto = (TaskPerRicerca) o;

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
		return "TipologiaRicerca{" + "id=" + id + ", keycode='" + keycode + "'"
				+ ", descrizione='" + descrizione + "'" + '}';
	}
}

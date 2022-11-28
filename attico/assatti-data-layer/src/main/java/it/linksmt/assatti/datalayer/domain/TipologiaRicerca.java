package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TipologiaRicerca.
 */
@Entity
@Table(name = "TIPOLOGIA_RICERCA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipologiaRicerca implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1076142251833855393L;

	public TipologiaRicerca() {
	}
	public TipologiaRicerca( final Long id ) {
		this.id=id;
	}

	public TipologiaRicerca(final Long id, final String code, final String descrizione) {
		this.id=id;
		this.code=code;
		this.descrizione=descrizione;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "descrizione")
	private String descrizione;
	
	@ManyToMany (fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "TIPOLOGIA_RICERCA_TASK" , joinColumns = { @JoinColumn(name = "tip_ricerca_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "task_id", nullable = false, updatable = false) })
    private Set<TaskPerRicerca> tasksPerRicerca = new HashSet<TaskPerRicerca>();

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(final String descrizione) {
		this.descrizione = descrizione;
	}
	
	public Set<TaskPerRicerca> getTasksPerRicerca() {
		return tasksPerRicerca;
	}
	public void setTasksPerRicerca(Set<TaskPerRicerca> tasksPerRicerca) {
		this.tasksPerRicerca = tasksPerRicerca;
	}
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TipologiaRicerca tipoAtto = (TipologiaRicerca) o;

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
		return "TipologiaRicerca{" + "id=" + id + ", code='" + code + "'"
				+ ", descrizione='" + descrizione + "'" + '}';
	}
}

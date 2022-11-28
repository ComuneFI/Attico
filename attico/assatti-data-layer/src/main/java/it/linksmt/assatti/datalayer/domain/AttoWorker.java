package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

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
 * Questa entity rappresenta i dati riguardanti i profili che hanno lavorato un determinato atto.
 * Una volta che l'iter per un determinato atto è terminato le informazioni vengono cancellate in
 * quanto questa tabella contiene soltanto le informazioni relative agli atti per cui esiste un
 * iter in corso.
 */
@Entity
@Table(name = "attoworker")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AttoWorker implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name="profilo_id", insertable = true, updatable = false)
	private Profilo profilo;
	
	@ManyToOne
	@JoinColumn(name="atto_id", insertable = true, updatable = false)
	private Atto atto;

	public Long getId() {
		return id;
	}

	public Profilo getProfilo() {
		return profilo;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atto == null) ? 0 : atto.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((profilo == null) ? 0 : profilo.hashCode());
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
		AttoWorker other = (AttoWorker) obj;
		if (atto == null) {
			if (other.atto != null)
				return false;
		} else if (!atto.equals(other.atto))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (profilo == null) {
			if (other.profilo != null)
				return false;
		} else if (!profilo.equals(other.profilo))
			return false;
		return true;
	}
	

}

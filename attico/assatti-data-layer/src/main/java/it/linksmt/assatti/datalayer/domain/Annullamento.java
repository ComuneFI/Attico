package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

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
 * Un Annullamento.
 */
@Entity
@Table(name = "annullamento")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Annullamento implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "stato")
    private String stato;

    @ManyToOne
    @JoinColumn(name="tipo" , insertable=true, updatable=true)
	private TipoAtto tipoAtto;

	public Long getId() {
		return id;
	}

	public String getStato() {
		return stato;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((stato == null) ? 0 : stato.hashCode());
		result = prime * result + ((tipoAtto == null) ? 0 : tipoAtto.hashCode());
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
		Annullamento other = (Annullamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (stato == null) {
			if (other.stato != null)
				return false;
		} else if (!stato.equals(other.stato))
			return false;
		if (tipoAtto == null) {
			if (other.tipoAtto != null)
				return false;
		} else if (!tipoAtto.equals(other.tipoAtto))
			return false;
		return true;
	}    
    
}

package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "tipoatto_has_fase_ricerca")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoAttoHasFaseRicerca implements Serializable, Comparable<TipoAttoHasFaseRicerca> {

	private static final long serialVersionUID = 1L;

	public TipoAttoHasFaseRicerca() {
	}
	public TipoAttoHasFaseRicerca( final Long id ) {
		this.id=id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="tipoatto_id", insertable = false, updatable = false)
	private TipoAtto tipoAtto;
	
	@ManyToOne
	@JoinColumn(name="fase_id", insertable = false, updatable = false)
	private FaseRicerca fase;
	
	@OneToMany(mappedBy = "faseTipoAtto", cascade= {CascadeType.PERSIST, CascadeType.MERGE})
	private Set<FaseRicercaHasCriterio> criteri = new HashSet<FaseRicercaHasCriterio>();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}
	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}
	public Set<FaseRicercaHasCriterio> getCriteri() {
		return criteri;
	}
	public void setCriteri(Set<FaseRicercaHasCriterio> criteri) {
		this.criteri = criteri;
	}
	
	public FaseRicerca getFase() {
		return fase;
	}
	public void setFase(FaseRicerca fase) {
		this.fase = fase;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoAttoHasFaseRicerca other = (TipoAttoHasFaseRicerca) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TipoAttoHasFaseRicerca [id=" + id + ", tipoAtto=" + tipoAtto + ", criteri=" + criteri + "]";
	}
	
	@Override
	public int compareTo(TipoAttoHasFaseRicerca o) {
		if(o!=null && o.getFase()!=null && o.getFase().getOrdine()!=null && this.getFase()!=null && this.getFase().getOrdine()!=null) {
			return this.getFase().getOrdine().compareTo(o.getFase().getOrdine());
		}else {
			return -1;
		}
	}

}

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "ricerca_fase_has_ricerca_criterio")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FaseRicercaHasCriterio implements Serializable, Comparable<FaseRicercaHasCriterio> {

	private static final long serialVersionUID = 1L;

	public FaseRicercaHasCriterio() {
	}
	public FaseRicercaHasCriterio( final Long id ) {
		this.id=id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "value")
	private Boolean value;
	
	@Column(name = "visibilita_completa")
	private Boolean visibilitaCompleta;

	@ManyToOne
	@JoinColumn(name="ricerca_criterio_id", insertable = false, updatable = false)
	private CriterioRicerca criterio;
	
	@ManyToOne
	@JoinColumn(name="ricerca_fase_tipoatto_id", insertable = false, updatable = false)
	private TipoAttoHasFaseRicerca faseTipoAtto;
	
	@ManyToMany (fetch=FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "ricerca_fase_criterio_has_aoo" , joinColumns = { @JoinColumn(name = "ricerca_fase_criterio_id", nullable = false, updatable = false, insertable =  false) }, inverseJoinColumns = { @JoinColumn(name = "aoo_id", nullable = false, updatable = false, insertable =  false) })
    private Set<Aoo> aoos = new HashSet<Aoo>();
	
	@ManyToMany (fetch=FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "ricerca_fase_criterio_has_profilo" , joinColumns = { @JoinColumn(name = "ricerca_fase_criterio_id", nullable = false, updatable = false, insertable =  false) }, inverseJoinColumns = { @JoinColumn(name = "profilo_id", nullable = false, updatable = false, insertable =  false) })
    private Set<Profilo> profilos = new HashSet<Profilo>();

	public Boolean getVisibilitaCompleta() {
		return visibilitaCompleta;
	}
	public void setVisibilitaCompleta(Boolean visibilitaCompleta) {
		this.visibilitaCompleta = visibilitaCompleta;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getValue() {
		return value;
	}
	public void setValue(Boolean value) {
		this.value = value;
	}
	public CriterioRicerca getCriterio() {
		return criterio;
	}
	public void setCriterio(CriterioRicerca criterio) {
		this.criterio = criterio;
	}
	public TipoAttoHasFaseRicerca getFaseTipoAtto() {
		return faseTipoAtto;
	}
	public void setFaseTipoAtto(TipoAttoHasFaseRicerca faseTipoAtto) {
		this.faseTipoAtto = faseTipoAtto;
	}
	public Set<Aoo> getAoos() {
		return aoos;
	}
	public void setAoos(Set<Aoo> aoos) {
		this.aoos = aoos;
	}
	public Set<Profilo> getProfilos() {
		return profilos;
	}
	public void setProfilos(Set<Profilo> profilos) {
		this.profilos = profilos;
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
		FaseRicercaHasCriterio other = (FaseRicercaHasCriterio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FaseRicercaHasCriterio [id=" + id + ", value=" + value + ", criterio=" + criterio + ", faseTipoAtto="
				+ faseTipoAtto + ", aoos=" + aoos + ", profilos=" +profilos +"]";
	}
	@Override
	public int compareTo(FaseRicercaHasCriterio o) {
		if(o!=null && o.getCriterio()!=null && o.getCriterio().getOrdine()!=null && this.getCriterio()!=null && this.getCriterio().getOrdine()!=null) {
			return this.getCriterio().getOrdine().compareTo(o.getCriterio().getOrdine());
		}else {
			return -1;
		}
	}
	
}

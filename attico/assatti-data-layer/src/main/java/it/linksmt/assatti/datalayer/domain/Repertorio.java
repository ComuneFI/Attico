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
 * Un repertorio.
 */
@Entity
@Table(name = "repertorio")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Repertorio implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@ManyToOne
    @JoinColumn(name="aoo_id" , insertable=true, updatable=false)
    private Aoo aoo;

    @ManyToOne
    @JoinColumn(name="tipoatto_id" , insertable=true, updatable=false)
	private TipoAtto tipoAtto;
    
    @Column(name="anno")
    private Integer anno;
    
    @Column(name="numero_repertorio")
    private Integer numero;

	public Long getId() {
		return id;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public Integer getAnno() {
		return anno;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((anno == null) ? 0 : anno.hashCode());
		result = prime * result + ((aoo == null) ? 0 : aoo.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
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
		Repertorio other = (Repertorio) obj;
		if (anno == null) {
			if (other.anno != null)
				return false;
		} else if (!anno.equals(other.anno))
			return false;
		if (aoo == null) {
			if (other.aoo != null)
				return false;
		} else if (!aoo.equals(other.aoo))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (tipoAtto == null) {
			if (other.tipoAtto != null)
				return false;
		} else if (!tipoAtto.equals(other.tipoAtto))
			return false;
		return true;
	}

    
}

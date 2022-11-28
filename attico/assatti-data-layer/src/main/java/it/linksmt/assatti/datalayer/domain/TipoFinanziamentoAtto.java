package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A tipofinanziamentoAtto.
 */
@Entity
@Table(name = "tipofinanziamento_atto")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoFinanziamentoAtto implements Serializable {

	 

	/**
	 * 
	 */
	private static final long serialVersionUID = -7262263458390384424L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="atto_id",insertable=true,updatable=false )
	private Atto atto;
	
	@ManyToOne
	@JoinColumn(name="tipoFinanziamento_id",insertable=true,updatable=false )
	private TipoFinanziamento tipoFinanziamento;
	
	
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	@Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TipoFinanziamentoAtto tipoFinanziamentoAtto = (TipoFinanziamentoAtto) o;

        if ( ! Objects.equals(id, tipoFinanziamentoAtto.id)) return false;

        return true;
    }

	
	 
	 
}

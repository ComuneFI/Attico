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
 * A DocumentoInformatico.
 */
@Entity
@Table(name = "ATTO_HAS_AMBITO_MATERIA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AttoHasAmbitoMateria implements Serializable {

	 

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="atto_id",insertable=true,updatable=false )
	private Atto atto;
	
	@ManyToOne
	@JoinColumn(name="ambitodl33_id",insertable=true,updatable=false )
	private AmbitoDl33 ambitoDl;
	
	@ManyToOne
	@JoinColumn(name="materiadl33_id",insertable=true,updatable=false )
	private MateriaDl33 materiaDl;
	
	
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

	public AmbitoDl33 getAmbitoDl() {
		return ambitoDl;
	}

	public void setAmbitoDl(AmbitoDl33 ambitoDl) {
		this.ambitoDl = ambitoDl;
	}

	public MateriaDl33 getMateriaDl() {
		return materiaDl;
	}

	public void setMateriaDl(MateriaDl33 materiaDl) {
		this.materiaDl = materiaDl;
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

        AttoHasAmbitoMateria attoHasAmbitoMateria = (AttoHasAmbitoMateria) o;

        if ( ! Objects.equals(id, attoHasAmbitoMateria.id)) return false;

        return true;
    }

	
	 
	 
}

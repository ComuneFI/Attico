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
 * A ProfiloComposizione.
 */
@Entity
@Table(name = "PROFILO_COMPOSIZIONE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProfiloComposizione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2541209230946357238L;


	public ProfiloComposizione(final Long id, final Composizione composizione, Profilo profilo, QualificaProfessionale qualificaProfessionale, Integer ordine, Boolean valido) {
		this.id=id;
		this.composizione=composizione;
		this.profilo=profilo;
		this.qualificaProfessionale =qualificaProfessionale;
		this.ordine=ordine;
		this.valido=valido;
	}



	public ProfiloComposizione() {
	}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
	@JoinColumn(name="id_composizione")
    private Composizione composizione;
	
	@ManyToOne
	@JoinColumn(name="id_profilo")
    private Profilo profilo;
	
	
	@ManyToOne
	@JoinColumn(name="id_qualifica_professionale")
    private QualificaProfessionale qualificaProfessionale;
	
	@Column(name = "ordine")
    private Integer ordine = new Integer(0);
   	
   	@Column(name = "valido", columnDefinition = "tinyint(1) default 0")
	private Boolean valido;


	public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
    
	public Composizione getComposizione() {
		return composizione;
	}

	public void setComposizione(Composizione composizione) {
		this.composizione = composizione;
	}

	public Profilo getProfilo() {
		return profilo;
	}

	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}

	public QualificaProfessionale getQualificaProfessionale() {
		return qualificaProfessionale;
	}

	public void setQualificaProfessionale(QualificaProfessionale qualificaProfessionale) {
		this.qualificaProfessionale = qualificaProfessionale;
	}

	public Integer getOrdine() {
		return ordine;
	}

	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}

	public Boolean getValido() {
		if (valido == null) {
			return false;
		}
		else {
			return valido;
		}
	}

	public void setValido(Boolean valido) {
		this.valido = valido;
	}
	@Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProfiloComposizione gruppoRuolo = (ProfiloComposizione) o;

        if ( ! Objects.equals(id, gruppoRuolo.id)) {
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
        return "ProfiloComposizione{" +
                "id=" + id +
                ", idProfilo='" + profilo.getId()+ "'" +
                ", idComposizione='" + composizione.getId()+ "'" +
                '}';
    }
}

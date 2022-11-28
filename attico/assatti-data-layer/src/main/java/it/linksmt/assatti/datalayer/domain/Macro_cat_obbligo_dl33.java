package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Macro_cat_obbligo_dl33.
 */
@Entity
@Table(name = "MACRO_CAT_OBBLIGO_DL33")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Macro_cat_obbligo_dl33 implements Serializable {

	public Macro_cat_obbligo_dl33() {
	}
	public Macro_cat_obbligo_dl33(Long id) {
		this.id=id;
	}
	
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codice")
    private String codice;
    
    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "attiva")
    private Boolean attiva;

    
    @OneToMany(mappedBy="fk_cat_obbligo_macro_cat_obbligo_idx")
    private Set<Cat_obbligo_dl33> categorie =   new HashSet<Cat_obbligo_dl33>();
    
    @Transient
	@JsonProperty
	private Boolean atti;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Boolean getAttiva() {
        return attiva;
    }

    public void setAttiva(Boolean attiva) {
        this.attiva = attiva;
    }
    
    
	public Set<Cat_obbligo_dl33> getCategorie() {
		return categorie;
	}

	public void setCategorie(Set<Cat_obbligo_dl33> categorie) {
		this.categorie = categorie;
	}

	public Boolean getAtti() {
		return atti;
	}
	
	public void setAtti(Boolean atti) {
		this.atti = atti;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Macro_cat_obbligo_dl33 macro_cat_obbligo_dl33 = (Macro_cat_obbligo_dl33) o;

        if ( ! Objects.equals(id, macro_cat_obbligo_dl33.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Macro_cat_obbligo_dl33{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", descrizione='" + descrizione + "'" +
                ", attiva='" + attiva + "'" +
                '}';
    }
}

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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Obbligo_DL33.
 */
@Entity
@Table(name = "OBBLIGO_DL33")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Obbligo_DL33 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codice")
    private String codice;
    
    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "attivo")
    private Boolean attivo;

    @ManyToOne
    private Cat_obbligo_dl33 cat_obbligo_DL33;

    @ManyToMany
    @OrderBy(value="ordine ASC")
    @JoinTable(name = "OBBLIGO_DL33_SCHEDA",
               joinColumns = @JoinColumn(name="obbligo_dl33s_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="schedas_id", referencedColumnName="ID"))
    private Set<Scheda> schedas = new HashSet<Scheda>();
    
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

    public Boolean getAttivo() {
        return attivo;
    }

    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }

    public Cat_obbligo_dl33 getCat_obbligo_DL33() {
        return cat_obbligo_DL33;
    }

    public void setCat_obbligo_DL33(Cat_obbligo_dl33 cat_obbligo_DL33) {
        this.cat_obbligo_DL33 = cat_obbligo_DL33;
    }

    public Set<Scheda> getSchedas() {
        return schedas;
    }

    public void setSchedas(Set<Scheda> schedas) {
        this.schedas = schedas;
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

        Obbligo_DL33 obbligo_DL33 = (Obbligo_DL33) o;

        if ( ! Objects.equals(id, obbligo_DL33.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Obbligo_DL33{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", descrizione='" + descrizione + "'" +
                ", attivo='" + attivo + "'" +
                '}';
    }
}

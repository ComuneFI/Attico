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
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GruppoRuolo.
 */
@Entity
@Table(name = "GRUPPORUOLO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GruppoRuolo implements Serializable {

	public GruppoRuolo(final Long id, final String denominazione) {
		this.id=id;
		this.denominazione=denominazione;

	}

	public GruppoRuolo(final Long id, final String denominazione, final Set<Ruolo> hasRuoli){
		this.id=id;
		this.denominazione=denominazione;
		this.hasRuoli = hasRuoli;
	}

	public GruppoRuolo() {
	}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "denominazione")
    private String denominazione;

    @ManyToMany (fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "RUOLO_HASGRUPPI" , joinColumns = { @JoinColumn(name = "grupporuolo_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "ruolo_id", nullable = false, updatable = false) })
    private Set<Ruolo> hasRuoli = new HashSet<Ruolo>();



	public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(final String denominazione) {
        this.denominazione = denominazione;
    }

	public Set<Ruolo> getHasRuoli() {
		return hasRuoli;
	}

	public void setHasRuoli(final Set<Ruolo> hasRuoli) {
		this.hasRuoli = hasRuoli;
	}

	@Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GruppoRuolo gruppoRuolo = (GruppoRuolo) o;

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
        return "GruppoRuolo{" +
                "id=" + id +
                ", denominazione='" + denominazione + "'" +
                '}';
    }
}

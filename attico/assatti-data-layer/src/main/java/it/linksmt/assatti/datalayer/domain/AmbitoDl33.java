package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortComparator;

import it.linksmt.assatti.datalayer.comparator.MateriaDl33Comparator;

/**
 * A AmbitoDl33.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "AMBITODL33")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AmbitoDl33 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "denominazione")
    private String denominazione;

    @Column(name = "attivo")
    private Boolean attivo;

    @SortComparator(MateriaDl33Comparator.class)
    @OneToMany(mappedBy="ambitoDl33")
    private SortedSet<MateriaDl33> materie;
  
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public Boolean getAttivo() {
        return attivo;
    }

    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }

	public SortedSet<MateriaDl33> getMaterie() {
		return materie;
	}

	public void setMaterie(SortedSet<MateriaDl33> materie) {
		this.materie = materie;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AmbitoDl33 ambitoDl33 = (AmbitoDl33) o;

        if ( ! Objects.equals(id, ambitoDl33.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AmbitoDl33{" +
                "id=" + id +
                ", denominazione='" + denominazione + "'" +
                ", attivo='" + attivo + "'" +
                '}';
    }
    
}

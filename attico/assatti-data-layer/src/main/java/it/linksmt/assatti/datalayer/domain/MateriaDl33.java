package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * A MateriaDl33.
 */
@Entity
@Table(name = "MATERIADL33")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MateriaDl33 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "denominazione")
    private String denominazione;

    @Column(name = "attivo")
    private Boolean attivo;

    @ManyToOne
    private AmbitoDl33 ambitoDl33;

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

    public AmbitoDl33 getAmbitoDl33() {
        return ambitoDl33;
    }

    public void setAmbitoDl33(AmbitoDl33 ambitoDl33) {
        this.ambitoDl33 = ambitoDl33;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MateriaDl33 materiaDl33 = (MateriaDl33) o;

        if ( ! Objects.equals(id, materiaDl33.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MateriaDl33{" +
                "id=" + id +
                ", denominazione='" + denominazione + "'" +
                ", attivo='" + attivo + "'" +
                '}';
    }
}

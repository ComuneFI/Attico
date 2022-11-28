package it.linksmt.assatti.datalayer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ArgomentoOdg.
 */
@Entity
@Table(name = "ARGOMENTOODG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ArgomentoOdg implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "descrizione")
    private String descrizione;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ArgomentoOdg argomentoOdg = (ArgomentoOdg) o;

        if ( ! Objects.equals(id, argomentoOdg.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ArgomentoOdg{" +
                "id=" + id +
                ", descrizione='" + descrizione + "'" +
                '}';
    }
}

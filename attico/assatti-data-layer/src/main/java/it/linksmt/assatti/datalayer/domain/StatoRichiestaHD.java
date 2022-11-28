package it.linksmt.assatti.datalayer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A StatoRichiestaHD.
 */
@Entity
@Table(name = "STATORICHIESTAHD")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StatoRichiestaHD implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 50)
    @Column(name = "descrizione", length = 50)
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

        StatoRichiestaHD statoRichiestaHD = (StatoRichiestaHD) o;

        if ( ! Objects.equals(id, statoRichiestaHD.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StatoRichiestaHD{" +
                "id=" + id +
                ", descrizione='" + descrizione + "'" +
                '}';
    }
}

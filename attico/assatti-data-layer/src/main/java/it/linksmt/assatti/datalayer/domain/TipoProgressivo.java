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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TipoProgressivo.
 */
@Entity
@Table(name = "TIPOPROGRESSIVO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoProgressivo implements Serializable {

	public TipoProgressivo() {
	}

    public TipoProgressivo(final Long id, final String descrizione) {
		this.id = id;
		this.descrizione = descrizione;
	}

	public TipoProgressivo(final Long id) {
		this.id = id;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "descrizione")
    private String descrizione;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(final String descrizione) {
        this.descrizione = descrizione;
    }

	@Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TipoProgressivo tipoProgressivo = (TipoProgressivo) o;

        if ( ! Objects.equals(id, tipoProgressivo.id)) {
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
        return "TipoProgressivo {" +
                "id=" + id +
                ", descrizione='" + descrizione + "'" +
                '}';
    }
}

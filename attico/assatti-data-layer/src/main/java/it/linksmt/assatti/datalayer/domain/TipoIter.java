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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TipoIter.
 */
@Entity
@Table(name = "TIPOITER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoIter implements Serializable {

	public TipoIter() {
	}

    public TipoIter(final Long id, final String descrizione, final TipoAtto tipoAtto) {
		this.id = id;
		this.descrizione = descrizione;
		this.tipoAtto = tipoAtto;
	}

	public TipoIter(final Long id) {
		this.id = id;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "codice")
    private String codice;

    @ManyToOne
    @JoinColumn(name="tipoatto_id" , insertable=true, updatable=false)
    private TipoAtto tipoAtto;

    @OneToMany(mappedBy="tipoiter")
    private Set<TipoAdempimento> tipiAdempimenti = new HashSet<TipoAdempimento>();

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

	public String getCodice() {
		return codice;
	}

	public void setCodice(final String codice) {
		this.codice = codice;
	}

	public Set<TipoAdempimento> getTipiAdempimenti() {
		return tipiAdempimenti;
	}

	public void setTipiAdempimenti(final Set<TipoAdempimento> tipiAdempimenti) {
		this.tipiAdempimenti = tipiAdempimenti;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAtto(final TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	@Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TipoIter tipoIter = (TipoIter) o;

        if ( ! Objects.equals(id, tipoIter.id)) {
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
        return "TipoIter{" +
                "id=" + id +
                ", descrizione='" + descrizione + "'" +
                '}';
    }
}

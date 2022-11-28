package it.linksmt.assatti.datalayer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TipoRichiestaHD.
 */
@Entity
@Table(name = "TIPORICHIESTAHD")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoRichiestaHD implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8702635871041813315L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 100)
    @Column(name = "descrizione", length = 100)
    private String descrizione;
    
    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "dirigente")
    private Boolean dirigente;

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

    public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getDirigente() {
		return dirigente;
	}

	public void setDirigente(Boolean dirigente) {
		this.dirigente = dirigente;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TipoRichiestaHD tipoRichiestaHD = (TipoRichiestaHD) o;

        if ( ! Objects.equals(id, tipoRichiestaHD.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TipoRichiestaHD{" +
                "id=" + id +
                ", descrizione='" + descrizione + "'" +
                '}';
    }
}

package it.linksmt.assatti.datalayer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TipoAoo.
 */
@Entity
@Table(name = "TIPOAOO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoAoo implements Serializable {

    public TipoAoo() {
		super();
	}

	public TipoAoo(Long id, String codice, String descrizione) {
		super();
		this.id = id;
		this.codice = codice;
		this.descrizione = descrizione;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	 @Column(name = "codice")
	 private String codice;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "note")
    private String note;
    
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

        TipoAoo tipoAoo = (TipoAoo) o;

        if ( ! Objects.equals(id, tipoAoo.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TipoAoo{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", descrizione='" + descrizione + "'" +
                ", note='" + note + "'" +
                '}';
    }
}

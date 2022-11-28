package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Ufficio.
 */
@Entity
@Table(name = "UFFICIO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ufficio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codice")
    private String codice;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "fax")
    private String fax;

    @Column(name = "email")
    private String email;

    @Column(name = "pec")
    private String pec;

    @ManyToOne
    private Profilo responsabile;

    @ManyToOne
    private Indirizzo indirizzo;

    @ManyToOne
    @JoinColumn(name="aoo_id",insertable=true)
    private Aoo aoo;

    @Embedded
    private Validita validita = new Validita();
    
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


    

    public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}

	public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPec() {
        return pec;
    }

    public void setPec(String pec) {
        this.pec = pec;
    }

    public Profilo getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(Profilo profilo) {
        this.responsabile = profilo;
    }

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    public Aoo getAoo() {
        return aoo;
    }

    public void setAoo(Aoo aoo) {
        this.aoo = aoo;
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

        Ufficio ufficio = (Ufficio) o;

        if ( ! Objects.equals(id, ufficio.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ufficio{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", descrizione='" + descrizione + "'" +
                ", fax='" + fax + "'" +
                ", email='" + email + "'" +
                ", pec='" + pec + "'" +
                '}';
    }
}

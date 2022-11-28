package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Assessorato.
 */
@Entity
@Table(name = "ASSESSORATO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Assessorato implements Serializable, IDestinatarioInterno {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Transient
    private String email;

    @Column(name = "denominazione")
    private String denominazione;

    @Column(name = "qualifica")
    private String qualifica;
    
    @Column(name = "codice")
    private String codice;
    
    @ManyToOne
    private Indirizzo indirizzo ; 
    
    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "fax")
    private String fax;
    
    @Column(name = "nominativo_responsabile")
    private String nominativoResponsabile;

    @Embedded
    private Validita validita = new Validita();

    @Column(name = "profiloresponsabile_id")
    private Long profiloResponsabileId;
    
    @Transient
    @JsonProperty
    private Profilo profiloResponsabile;

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

    public String getQualifica() {
        return qualifica;
    }

    public void setQualifica(String qualifica) {
        this.qualifica = qualifica;
    }


    public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}

	public Profilo getProfiloResponsabile() {
        return profiloResponsabile;
    }

	public Long getProfiloResponsabileId() {
		return profiloResponsabileId;
	}

	public void setProfiloResponsabileId(Long profiloResponsabileId) {
		this.profiloResponsabileId = profiloResponsabileId;
	}
	
    public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public Indirizzo getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(Indirizzo indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getNominativoResponsabile() {
		return nominativoResponsabile;
	}

	public void setNominativoResponsabile(String nominativoResponsabile) {
		this.nominativoResponsabile = nominativoResponsabile;
	}

	public void setProfiloResponsabile(Profilo profilo) {
        this.profiloResponsabile = profilo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Assessorato assessorato = (Assessorato) o;

        if ( ! Objects.equals(id, assessorato.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Assessorato{" +
                "id=" + id +
                ", denominazione='" + denominazione + "'" +
                ", qualifica='" + qualifica + "'" +
                '}';
    }

	@Override
	public String getClassName() {
		return Assessorato.class.getCanonicalName();
	}

	@Override
	public String getDescrizioneAsDestinatario() {
		if(qualifica!=null){
			return denominazione + " (" + qualifica + ")";
		}else{
			return denominazione;
		}
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getPec() {
		return null;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

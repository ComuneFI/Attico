package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.dto.UserDTO;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Utente.
 */
@Entity
@Table(name = "UTENTE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Utente implements Serializable, IDestinatarioInterno {

	   /**
	 * 
	 */
	private static final long serialVersionUID = -7365319798533154226L;

	public Utente(Utente utente) {
		   this.id= utente.getId();
		   this.username = utente.getUsername();
		}

	public Utente() {
	}
	
	public Utente(Long id, String codicefiscale, String username) {
		  this.id= id;
		   this.username = username;
		   this.codicefiscale = codicefiscale;
	}
	
	public Utente(Long id, String codicefiscale, String username, String cognome, String nome) {
		  this.id= id;
		   this.username = username;
		   this.codicefiscale = codicefiscale;
		   this.cognome = cognome;
		   this.nome = nome;
	}

	@Transient
	@JsonProperty
	UserDTO user;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="moduloregistrazione",insertable=true, updatable=false)
    private File moduloregistrazione;
    
    public File getModuloregistrazione() {
		return moduloregistrazione;
	}

	public void setModuloregistrazione(File moduloregistrazione) {
		this.moduloregistrazione = moduloregistrazione;
	}

	@Column(name = "cognome")
    private String cognome;
	
	@Column(name="lastusedprofileid")
	private Long lastProfileId;

    @Column(name = "nome")
    private String nome;

    @Column(name = "username",nullable=false,unique=true)
    @NotNull
    private String username;

    @Column(name = "codicefiscale",nullable=false)
    @NotNull
    private String codicefiscale;

    @Embedded
    private Validita validita = new Validita();

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "altrorecapito")
    private String altrorecapito;

    @Column(name = "fax")
    private String fax;

    @Column(name = "email")
    @NotNull
    @Email
    private String email;

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE})
    private Indirizzo indirizzo ; 

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "utente")
    @Where(clause = "validita.validoal is null")
    @JsonBackReference
	private Set<Profilo> hasProfili = new HashSet<Profilo>( );
    
    @Column(name = "stato",nullable=false)
    @Enumerated
    @NotNull
    private UtenteStato stato;

    @Transient
    @JsonSerialize
    private List<String> aoos;
 
	public List<String> getAoos() {
		return aoos;
	}

	public void setAoos(List<String> aoos) {
		this.aoos = aoos;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCodicefiscale() {
        return codicefiscale;
    }


    public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}

	public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAltrorecapito() {
        return altrorecapito;
    }

    public void setAltrorecapito(String altrorecapito) {
        this.altrorecapito = altrorecapito;
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

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

 

	public Set<Profilo> getHasProfili() {
		return hasProfili;
	}

	public void setHasProfili(Set<Profilo> hasProfili) {
		this.hasProfili = hasProfili;
	}

	
	public UtenteStato getStato() {
		return stato;
	}

	public void setStato(UtenteStato stato) {
		this.stato = stato;
	}


    public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Utente utente = (Utente) o;

        if ( ! Objects.equals(id, utente.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "Utente [user=" + user + ", id=" + id + ", moduloregistrazione=" + moduloregistrazione + ", cognome="
				+ cognome + ", nome=" + nome + ", username=" + username + ", codicefiscale=" + codicefiscale
				 + ", telefono=" + telefono + ", altrorecapito=" + altrorecapito + ", fax="
				+ fax + ", email=" + email + ", indirizzo=" + indirizzo + ", stato="
				+ stato + "]";
	}

	@Override
	public String getClassName() {
		return Utente.class.getCanonicalName();
	}

	@Override
	public String getDescrizioneAsDestinatario() {
		return cognome + " " + nome + " (C.F. " + codicefiscale + ")<br/> Nome Utente: " + username;
	}

	@Override
	public String getPec() {
		return null;
	}

	public Long getLastProfileId() {
		return lastProfileId;
	}

	public void setLastProfileId(Long lastProfileId) {
		this.lastProfileId = lastProfileId;
	}

	
}

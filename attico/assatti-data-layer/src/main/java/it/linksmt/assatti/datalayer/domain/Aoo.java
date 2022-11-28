package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Aoo.
 */
@Entity
@Table(name = "AOO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Aoo implements Serializable, IDestinatarioInterno, Comparable<Aoo> {

    public Aoo(Aoo padre) {
    	this.id=padre.getId();
    	this.descrizione=padre.getDescrizione();
    	this.codice=padre.getCodice();
    	this.identitavisiva=padre.getIdentitavisiva();
	}
    
    public Aoo( Long id, String descrizione, String codice, String identita) {
    	this.id=id;
    	this.descrizione=descrizione;
    	this.codice=codice;
    	this.identitavisiva=identita;
  	}
    
    public Aoo( Long id, String descrizione, String codice, String identita, String logo) {
    	this.id=id;
    	this.descrizione=descrizione;
    	this.codice=codice;
    	this.identitavisiva=identita;
    	this.logo = logo;
  	}
    
    public Aoo(Long id) {
    	this.id=id;
  	}
    
    public Aoo() {
  	}
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codice",insertable=true,updatable=false)
    private String codice;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "fax")
    private String fax;

    @Column(name = "email")
    private String email;

    @Column(name = "pec")
    private String pec;

    @Column(name = "identitavisiva")
    private String identitavisiva;
    
    @Column(name = "specializzazione")
    @Basic(fetch=FetchType.EAGER)
    private String specializzazione;
    
    @Column(name="uo")
	private Boolean uo;

    @Embedded
    Validita validita = new Validita();
    
    @Column(name = "profiloresponsabile_id")
    private Long profiloResponsabileId;
    
    @Transient
    @JsonProperty
    private Profilo profiloResponsabile;
    
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "AOO_HASASSESSORATI" , joinColumns = { @JoinColumn(name = "aoo_id", nullable = false,insertable =  false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "assessorato_id", nullable = false,insertable =  false, updatable = false) })
    private Set<Assessorato> hasAssessorati = new HashSet<Assessorato>();

    /*@ManyToOne
    private DenominazioneRelatore denominazioneRelatore;*/

    @ManyToOne(  cascade={CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE} )
    private Indirizzo indirizzo;

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private TipoAoo tipoAoo;

    @Lob
    @Type(type="org.hibernate.type.MaterializedClobType")
    @Column(name = "logo")
    private String logo;
    
    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "aooPadre",insertable=true,updatable=true)
    private Aoo aooPadre;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aooPadre")
    private Set<Aoo> sottoAoo = new HashSet<Aoo>(0);
    
    @JsonIgnore
    @Basic(fetch=FetchType.LAZY)
    @Formula("(select GetSuperAooIds(id))")
    private String superAooIds;
   
//    @OneToMany(fetch = FetchType.LAZY , mappedBy = "aoo" )
//   	private Set<TipoMateria> tipoMaterie = new HashSet<TipoMateria>(0);
    
//    @OneToMany(fetch = FetchType.LAZY  , mappedBy = "aoo" )
//	private Set<Ufficio> uffici = new HashSet<Ufficio>(0);

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aoo")
//	private Set<Profilo> profili = new HashSet<Profilo>(0);
//    
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aoo")
//	private Set<GruppoRuolo> grupporuoli = new HashSet<GruppoRuolo>(0);
   
  

    @Transient
    
	public String dataType ="aoo";
   
    @Transient
    @JsonSerialize
	public String stato=null;
 
    @JsonSerialize
	public String getStato() {
    	if(this.stato == null ){
	    	if(this.validita == null ){
				this.stato= "ATTIVO";
			}else if(this.validita.getValidoal() == null ){
				this.stato= "ATTIVO";
			}else{
				this.stato= "NON ATTIVO";
			}
    	}
    	
    	
		return this.stato;
	}
    
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public Long getId() {
        return id;
    }

    public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
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

    public String getIdentitavisiva() {
        return identitavisiva;
    }

    public void setIdentitavisiva(String identitavisiva) {
        this.identitavisiva = identitavisiva;
    }

    public Validita getValidita() {
		return validita;
	}
	public void setValidita(Validita validita) {
		this.validita = validita;
	}

    /*public DenominazioneRelatore getDenominazioneRelatore() {
        return denominazioneRelatore;
    }

    public void setDenominazioneRelatore(DenominazioneRelatore DenominazioneRelatore) {
        this.denominazioneRelatore = DenominazioneRelatore;
    }*/

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getSuperAooIds() {
		return superAooIds;
	}

	public void setSuperAooIds(String superAooIds) {
		this.superAooIds = superAooIds;
	}

	public TipoAoo getTipoAoo() {
        return tipoAoo;
    }

    public void setTipoAoo(TipoAoo tipoAoo) {
        this.tipoAoo = tipoAoo;
    }
    

	public Aoo getAooPadre() {
		return aooPadre;
	}

	public void setAooPadre(Aoo aooPadre) {
		this.aooPadre = aooPadre;
	}

	public Set<Aoo> getSottoAoo() {
		return sottoAoo;
	}

	public void setSottoAoo(Set<Aoo> sottoAoo) {
		this.sottoAoo = sottoAoo;
	}
	
	

//	public Set<Ufficio> getUffici() {
//		return uffici;
//	}
//
//	public void setUffici(Set<Ufficio> uffici) {
//		this.uffici = uffici;
//	}

//	public Set<GruppoRuolo> getGrupporuoli() {
//		return grupporuoli;
//	}
//
//	public void setGrupporuoli(Set<GruppoRuolo> grupporuoli) {
//		this.grupporuoli = grupporuoli;
//	}
//
//	public Set<TipoMateria> getTipoMaterie() {
//		return tipoMaterie;
//	}
//
//	public void setTipoMaterie(Set<TipoMateria> tipoMaterie) {
//		this.tipoMaterie = tipoMaterie;
//	}
//
//	public Set<Profilo> getProfili() {
//		return profili;
//	}
//
//	public void setProfili(Set<Profilo> profili) {
//		this.profili = profili;
//	}

 
	public Set<Assessorato> getHasAssessorati() {
		return hasAssessorati;
	}

	public void setHasAssessorati(Set<Assessorato> hasAssessorati) {
		this.hasAssessorati = hasAssessorati;
	}

	public Long getProfiloResponsabileId() {
		return profiloResponsabileId;
	}

	public void setProfiloResponsabileId(Long profiloResponsabileId) {
		this.profiloResponsabileId = profiloResponsabileId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Aoo aoo = (Aoo) o;

        if ( ! Objects.equals(id, aoo.id)) return false;

        return true;
    }
	

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Aoo{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", descrizione='" + descrizione + "'" +
                ", telefono='" + telefono + "'" +
                ", fax='" + fax + "'" +
                ", email='" + email + "'" +
                ", pec='" + pec + "'" +
                ", identitavisiva='" + identitavisiva + "'" +
                '}';
    }

	public Profilo getProfiloResponsabile() {
		return profiloResponsabile;
	}

	public void setProfiloResponsabile(Profilo profiloResponsabile) {
		this.profiloResponsabile = profiloResponsabile;
	}

	@Override
	public String getClassName() {
		return Aoo.class.getCanonicalName();
	}

	@Override
	public String getDescrizioneAsDestinatario() {
		return codice + " - " + descrizione;
	}

	@Override
	public int compareTo(Aoo o) {
		return this.codice.compareTo(o.getCodice());
	}

	public Boolean getUo() {
		return uo;
	}

	public void setUo(Boolean uo) {
		this.uo = uo;
	}

	public String getSpecializzazione() {
		return specializzazione;
	}

	public void setSpecializzazione(String specializzazione) {
		this.specializzazione = specializzazione;
	}
}

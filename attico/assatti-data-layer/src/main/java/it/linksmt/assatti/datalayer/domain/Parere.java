package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

/**
 * A Parere.
 */
@Entity
@Table(name = "PARERE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Parere  extends AbstractAuditingEntity implements Serializable {

	public Parere() {
	}
	
    public Parere(Long id) {
		super();
		this.id = id;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@ManyToOne
    @JoinColumn(
        name = "tipo_azione", 
        referencedColumnName = "codice"
    )
    private TipoAzione tipoAzione;

    @Column(name = "titolo")
    private String titolo;
    
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Type(type="org.hibernate.type.MaterializedClobType")
    @Column(name = "parere")
    private String parere;
    
    @Column(name = "annullato")
   	private Boolean annullato;

    @Column(name = "pareres_sintetico")
    private String parereSintetico;

    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data")
    private DateTime data;
    
    @Column(name = "origine")
    private String origine;
    
    @Column(name = "stato_risposta")
    private String statoRisposta;
    
    @Column(name = "termini_presentazione")
    private String terminiPresentazione;
    
    @Column(name = "descrizione_qualifica")
    private String descrizioneQualifica;
    
    @Column(name = "parere_personalizzato")
    private String parerePersonalizzato;

    @ManyToOne
    @JoinColumn(name="atto_id",insertable=true, updatable=false)
    private Atto atto;
    
    @ManyToOne
    @JoinColumn(name="profilo_id",insertable=true, updatable=true)
    private Profilo profilo;
    
    @ManyToOne
    @JoinColumn(name="profilo_relatore_id",insertable=true, updatable=true)
    private Profilo profiloRelatore;

    @ManyToOne
    @JoinColumn(name="aoo_id",insertable=true,updatable=false)
    private Aoo aoo;
 

    @OneToMany
    @JoinColumn(name="parere_id", insertable=true, updatable=false)
   	@OrderBy(value = "created_date DESC")
   	private List<DocumentoPdf> documentiPdf = new ArrayList<DocumentoPdf>();
    
    @Transient
    private Utente createUser;

//	@OneToMany(mappedBy = "parere" , cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)  
//	@OrderBy(value = " ordine_firma ASC" )
//	private Set<SottoscrittoreParere> sottoscrittori = new TreeSet<SottoscrittoreParere>();

	@OneToMany(mappedBy = "parere",   cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
	@OrderBy(value = " ordine_inclusione ASC")
	private Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_invio")
    private DateTime dataInvio;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_sollecito")
    private DateTime dataSollecito;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_scadenza")
    private DateTime dataScadenza;
	
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParere() {
        return parere;
    }

    public void setParere(String parere) {
        this.parere = parere;
    }

    public Boolean getAnnullato() {
		return annullato;
	}

	public void setAnnullato(Boolean annullato) {
		this.annullato = annullato;
	}

	public List<DocumentoPdf> getDocumentiPdf() {
		return documentiPdf;
	}

	public void setDocumentiPdf(List<DocumentoPdf> documentiPdf) {
		this.documentiPdf = documentiPdf;
	}

	public String getParereSintetico() {
		return parereSintetico;
	}

	public void setParereSintetico(String parereSintetico) {
		this.parereSintetico = parereSintetico;
	}
	
	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public String getStatoRisposta() {
		return statoRisposta;
	}

	public void setStatoRisposta(String statoRisposta) {
		this.statoRisposta = statoRisposta;
	}

	public String getTerminiPresentazione() {
		return terminiPresentazione;
	}

	public void setTerminiPresentazione(String terminiPresentazione) {
		this.terminiPresentazione = terminiPresentazione;
	}

	public TipoAzione getTipoAzione() {
		return tipoAzione;
	}

	public void setTipoAzione(TipoAzione tipoAzione) {
		this.tipoAzione = tipoAzione;
	}
	
//	public Set<SottoscrittoreParere> getSottoscrittori() {
//	return sottoscrittori;
//}
//
//public void setSottoscrittori(Set<SottoscrittoreParere> sottoscrittori) {
//	this.sottoscrittori = sottoscrittori;
//}

	public Set<DocumentoInformatico> getAllegati() {
		return allegati;
	}

	public void setAllegati(Set<DocumentoInformatico> allegati) {
		this.allegati = allegati;
	}

    public DateTime getData() {
		return data;
	}

	public void setData(DateTime data) {
		this.data = data;
	}

	public Atto getAtto() {
        return atto;
    }

    public Profilo getProfilo() {
		return profilo;
	}

	public String getDescrizioneQualifica() {
		return descrizioneQualifica;
	}

	public void setDescrizioneQualifica(String descrizioneQualifica) {
		this.descrizioneQualifica = descrizioneQualifica;
	}

	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}
	
	public Profilo getProfiloRelatore() {
		return profiloRelatore;
	}

	public void setProfiloRelatore(Profilo profiloRelatore) {
		this.profiloRelatore = profiloRelatore;
	}

	public void setAtto(Atto atto) {
        this.atto = atto;
    }

    public Aoo getAoo() {
        return aoo;
    }

    public void setAoo(Aoo aoo) {
        this.aoo = aoo;
    }

    public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public DateTime getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(DateTime dataInvio) {
		this.dataInvio = dataInvio;
	}

	public DateTime getDataSollecito() {
		return dataSollecito;
	}

	public void setDataSollecito(DateTime dataSollecito) {
		this.dataSollecito = dataSollecito;
	}

	public DateTime getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(DateTime dataScadenza) {
		this.dataScadenza = dataScadenza;
	}
	
	public String getParerePersonalizzato() {
		return parerePersonalizzato;
	}

	public void setParerePersonalizzato(String parerePersonalizzato) {
		this.parerePersonalizzato = parerePersonalizzato;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Parere parere = (Parere) o;

        if ( ! Objects.equals(id, parere.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Parere{" +
                "id=" + id +
                ", tipoParere='" + tipoAzione!=null&&tipoAzione.getCodice()!=null?tipoAzione.getCodice():"" + "'" +
                ", parere='" + parere + "'" +
                ", data='" + data + "'" +
                ", titolo='" + titolo + "'" +
                '}';
    }

	public Utente getCreateUser() {
		return createUser;
	}

	public void setCreateUser(Utente createUser) {
		this.createUser = createUser;
	}
}

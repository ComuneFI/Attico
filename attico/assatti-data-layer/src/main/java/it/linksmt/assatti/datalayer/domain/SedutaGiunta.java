package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A SedutaGiunta.
 */
@Entity
@Table(name = "SEDUTAGIUNTA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SedutaGiunta implements Serializable {

	public SedutaGiunta() {
	}
	
	public SedutaGiunta(Long id, String luogo, DateTime dataOra) {
		this.id = id;
		this.luogo = luogo;
		this.dataOra = dataOra;
	}
	
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "luogo")
    private String luogo;
    
    @Column(name = "protocollo")
    private String protocollo;
    
    @Column(name = "organo", insertable = true, updatable = false)
    private String organo;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_ora")
    private DateTime dataOra;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "prima_convocazione_inizio")
    private DateTime primaConvocazioneInizio;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "prima_convocazione_fine")
    private DateTime primaConvocazioneFine;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "seconda_convocazione_inizio")
    private DateTime secondaConvocazioneInizio;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "inizio_lavori_effettiva")
    private DateTime inizioLavoriEffettiva;
    
    @Column(name = "seconda_convocazione_luogo")
    private String secondaConvocazioneLuogo;

	@Column(name = "stato")
    private String stato;
	
	@Column(name = "fase")
    private String fase;

    @Column(name = "tipo_seduta")
    private Integer tipoSeduta;

    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "presidente_id")
    private Profilo presidente;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vicepresidente_id")
    private Profilo vicepresidente;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "segretario_id")
	private Profilo segretario;
	
//    @ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "COMPONENTI_GIUNTA", joinColumns = { @JoinColumn(name = "sedutagiunta_id" ,nullable=false) }, inverseJoinColumns = { @JoinColumn(name = "profilo_id",nullable=false) })
//	private Set<Profilo> componentiGiunta = new HashSet<Profilo>( );
    
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "RUBRICA_SEDUTA", joinColumns = { @JoinColumn(name = "sedutagiunta_id" ,nullable=false) }, inverseJoinColumns = { @JoinColumn(name = "rubricadestinatarioesterno_id",nullable=false) })
	private Set<RubricaDestinatarioEsterno> rubricaSeduta = new HashSet<RubricaDestinatarioEsterno>( );
    
    @Transient
	@JsonProperty
	private Set<AttoHasDestinatario> destinatariInterni = new HashSet<AttoHasDestinatario>();

   	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sedutariferimento_id")
	private SedutaGiunta sedutariferimento;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sedutariferimento")
	private Set<SedutaGiunta> sedutaGiuntas = new HashSet<SedutaGiunta>(0);

//	private Set<Lettera> letteras = new HashSet<Lettera>(0);
	
	@OneToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE}, orphanRemoval=true)
	private Verbale verbale;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sedutaGiunta",cascade={CascadeType.ALL}, orphanRemoval=true)
	@OrderBy(value = "id ASC")
	private Set<OrdineGiorno> odgs = new HashSet<OrdineGiorno>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sedutaGiunta")
	private Set<Resoconto> resoconto = new HashSet<Resoconto>();
	
	@Column(name = "not_tutti_assessori")
    private Boolean notificaTuttiAssessori;
	
	@Column(name = "not_tutti_consiglieri")
    private Boolean notificaTuttiConsiglieri;
	
	@Column(name = "not_tutti_altrestrutture")
    private Boolean notificaTuttiAltreStrutture;
	
	@Column(name = "numero")
	private String numero;
	
	@ManyToOne
	@JoinColumn(name="sottoscrittore_variazione_profilo_id")
    private Profilo sottoscrittoreDocVariazione;
	
	@ManyToOne
	@JoinColumn(name="sottoscrittore_annullamento_profilo_id")
    private Profilo sottoscrittoreDocAnnullamento;
	
	@Column(name = "stato_documento")
	private String statoDocumento;

	@ManyToOne
	@JoinColumn(name="sottoscrittore_documento_id")
    private Profilo sottoscittoreDocumento;

	@OneToMany(mappedBy = "sedutaresoconto", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@OrderBy(value = " ordine_firma ASC")
	private Set<SottoscrittoreSedutaGiunta> sottoscrittoriresoconto = new TreeSet<SottoscrittoreSedutaGiunta>();
	
	public Set<SottoscrittoreSedutaGiunta> getSottoscrittoriresoconto() {
		return sottoscrittoriresoconto;
	}

	public void setSottoscrittoriresoconto(Set<SottoscrittoreSedutaGiunta> sottoscrittoriresoconto) {
		this.sottoscrittoriresoconto = sottoscrittoriresoconto;
	}

	public Set<Resoconto> getResoconto() {
		return resoconto;
	}

	public void setResoconto(Set<Resoconto> resoconto) {
		this.resoconto = resoconto;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }
    
    public String getProtocollo() {
		return protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}

	public Integer getTipoSeduta() {
        return tipoSeduta;
    }

    public void setTipoSeduta(Integer tipoSeduta) {
        this.tipoSeduta = tipoSeduta;
    }

    public Profilo getPresidente() {
		return presidente;
	}

	public void setPresidente(Profilo presidente) {
		this.presidente = presidente;
	}

	public Profilo getSegretario() {
		return segretario;
	}

	public void setSegretario(Profilo segretario) {
		this.segretario = segretario;
	}

	public SedutaGiunta getSedutariferimento() {
		return sedutariferimento;
	}

	public void setSedutariferimento(SedutaGiunta sedutariferimento) {
		this.sedutariferimento = sedutariferimento;
	}

	public Set<SedutaGiunta> getSedutaGiuntas() {
		return sedutaGiuntas;
	}

	public void setSedutaGiuntas(Set<SedutaGiunta> sedutaGiuntas) {
		this.sedutaGiuntas = sedutaGiuntas;
	}
	
	public Set<AttoHasDestinatario> getDestinatariInterni() {
		return destinatariInterni;
	}

	public void setDestinatariInterni(Set<AttoHasDestinatario> destinatariInterni) {
		this.destinatariInterni = destinatariInterni;
	}

	public Set<OrdineGiorno> getOdgs() {
		return odgs;
	}

	public void setOdgs(Set<OrdineGiorno> odgs) {
		this.odgs = odgs;
	}
	
	public DateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(DateTime dataOra) {
        this.dataOra = dataOra;
    }
    
	public DateTime getPrimaConvocazioneInizio() {
		return primaConvocazioneInizio;
	}

	public void setPrimaConvocazioneInizio(DateTime primaConvocazioneInizio) {
		this.primaConvocazioneInizio = primaConvocazioneInizio;
	}
	
	public DateTime getInizioLavoriEffettiva() {
		return inizioLavoriEffettiva;
	}

	public void setInizioLavoriEffettiva(DateTime inizioLavoriEffettiva) {
		this.inizioLavoriEffettiva = inizioLavoriEffettiva;
	}

	public DateTime getPrimaConvocazioneFine() {
		return primaConvocazioneFine;
	}

	public void setPrimaConvocazioneFine(DateTime primaConvocazioneFine) {
		this.primaConvocazioneFine = primaConvocazioneFine;
	}

	public DateTime getSecondaConvocazioneInizio() {
		return secondaConvocazioneInizio;
	}

	public void setSecondaConvocazioneInizio(DateTime secondaConvocazioneInizio) {
		this.secondaConvocazioneInizio = secondaConvocazioneInizio;
	}

	public String getSecondaConvocazioneLuogo() {
		return secondaConvocazioneLuogo;
	}

	public void setSecondaConvocazioneLuogo(String secondaConvocazioneLuogo) {
		this.secondaConvocazioneLuogo = secondaConvocazioneLuogo;
	}

	public Profilo getVicepresidente() {
		return vicepresidente;
	}

	public void setVicepresidente(Profilo vicepresidente) {
		this.vicepresidente = vicepresidente;
	}
	
	public Set<RubricaDestinatarioEsterno> getRubricaSeduta() {
		return rubricaSeduta;
	}

	public void setRubricaSeduta(Set<RubricaDestinatarioEsterno> rubricaSeduta) {
		this.rubricaSeduta = rubricaSeduta;
	}
	
	public Verbale getVerbale() {
		return verbale;
	}

	public void setVerbale(Verbale verbale) {
		this.verbale = verbale;
	}
	
	public Boolean getNotificaTuttiAssessori() {
		return notificaTuttiAssessori;
	}

	public void setNotificaTuttiAssessori(Boolean notificaTuttiAssessori) {
		this.notificaTuttiAssessori = notificaTuttiAssessori;
	}

	public Boolean getNotificaTuttiConsiglieri() {
		return notificaTuttiConsiglieri;
	}

	public void setNotificaTuttiConsiglieri(Boolean notificaTuttiConsiglieri) {
		this.notificaTuttiConsiglieri = notificaTuttiConsiglieri;
	}

	public Boolean getNotificaTuttiAltreStrutture() {
		return notificaTuttiAltreStrutture;
	}

	public void setNotificaTuttiAltreStrutture(Boolean notificaTuttiAltreStrutture) {
		this.notificaTuttiAltreStrutture = notificaTuttiAltreStrutture;
	}
	
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public Profilo getSottoscrittoreDocVariazione() {
		return sottoscrittoreDocVariazione;
	}

	public void setSottoscrittoreDocVariazione(Profilo sottoscrittoreDocVariazione) {
		this.sottoscrittoreDocVariazione = sottoscrittoreDocVariazione;
	}

	public Profilo getSottoscrittoreDocAnnullamento() {
		return sottoscrittoreDocAnnullamento;
	}

	public void setSottoscrittoreDocAnnullamento(Profilo sottoscrittoreDocAnnullamento) {
		this.sottoscrittoreDocAnnullamento = sottoscrittoreDocAnnullamento;
	}

	public String getStatoDocumento() {
		return statoDocumento;
	}

	public void setStatoDocumento(String statoDocumento) {
		this.statoDocumento = statoDocumento;
	}

	public Profilo getSottoscittoreDocumento() {
		return sottoscittoreDocumento;
	}

	public void setSottoscittoreDocumento(Profilo sottoscittoreDocumento) {
		this.sottoscittoreDocumento = sottoscittoreDocumento;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SedutaGiunta sedutaGiunta = (SedutaGiunta) o;

        if ( ! Objects.equals(id, sedutaGiunta.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SedutaGiunta{" +
                "id=" + id +
                ", luogo='" + luogo + "'" +
              //  ", vicepresidente='" + vicepresidente + "'" +
               // ", presidente='" + presidente + "'" +
               ", primaConvocazioneInizio='" + primaConvocazioneInizio + "'" +
               ", inizioLavoriEffettiva='" + inizioLavoriEffettiva + "'" +
                ", stato='" + stato + "'" +
                ", tipoSeduta='" + tipoSeduta + "'" +
                '}';
    }
}

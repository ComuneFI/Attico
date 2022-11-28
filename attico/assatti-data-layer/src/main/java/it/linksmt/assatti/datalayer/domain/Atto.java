package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

/**
 * A Atto.
 */
@Entity
@Table(name = "ATTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Atto extends AbstractAuditingEntity implements Serializable, Comparable<Atto> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Atto(final Long id) {
		this.id = id;
	}

	public Atto() {
	}
	
	/**
	 * Viene calcolato in base alla configurazione del tipo di atto
	 * Assume valore I se in itinere, C se concluso
	 */
	@JsonIgnore
    @Basic(fetch=FetchType.LAZY)
    @Formula("(select CheckStatusIterByAttoId(id))")
	private String iterStatus;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany(mappedBy = "atto", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	@OrderBy("data DESC")
	private Set<Nota> listaNote = new HashSet<Nota>();
	
	@Column(name = "ie")
	private Boolean ie;

	@Column(name = "regolamento")
	private String regolamento;

	@Column(name = "motivoclonazione")
	String motivoClonazione;

	@Column(name = "codice_cifra", insertable = true, unique = true)
	// nullable = false,
	// updatable = false,
	// @NotNull
	private String codiceCifra;

	@Column(name = "oggetto")
	private String oggetto;

	@Column(name = "riservato")
	private Boolean riservato;

	@Column(name = "pubblicazione_integrale")
	private Boolean pubblicazioneIntegrale;

	@Column(name = "notifica_beneficiari", columnDefinition = "tinyint(1) default 1")
	private Boolean notificaBeneficiari;

	@Column(name = "burp")
	private Boolean burp;

	@Column(name = "codicecifra_atto_provvisorio")
	private String codicecifraAttoProvvisorio;

	@Column(name = "numero_adozione_atto_provvisorio")
	private String numeroAdozioneAttoProvvisorio;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "dataadozione_atto_provvisorio")
	private LocalDate dataAdozioneAttoProvvisorio;

	@Column(name = "codicecifra_atto_revocato")
	private String codicecifraAttoRevocato;

	@Column(name = "numero_adozione_atto_revocato")
	private String numeroAdozioneAttoRevocato;

	@Column(name = "durata_giorni")
	private Integer durataGiorni;

	@Column(name = "oblio")
	private Boolean oblio = false;

	@Column(name = "infoattodelega", length = 150)
	private String infoattodelega;

	@Column(name = "congiunto")
	private Boolean congiunto;

	@Column(name = "contabile_oggetto")
	private String contabileOggetto;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "garanzie_riservatezza_id", insertable = true, updatable = false)
	private SezioneTesto garanzieRiservatezza = new SezioneTesto();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "dichiarazioni_id", insertable = true, updatable = false)
	private SezioneTesto dichiarazioni = new SezioneTesto();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "inf_anagrafico_contabili_id", insertable = true, updatable = false)
	private SezioneTesto informazioniAnagraficoContabili = new SezioneTesto();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "dispositivo_id", insertable = true, updatable = false)
	private SezioneTesto dispositivo = new SezioneTesto();
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "domanda_id", insertable = true, updatable = false)
	private SezioneTesto domanda = new SezioneTesto();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "adempimenti_contabili_id", insertable = true, updatable = false)
	private SezioneTesto adempimentiContabili = new SezioneTesto();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "note_motivazione_id", insertable = true, updatable = false)
	private SezioneTesto noteMotivazione = new SezioneTesto();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "preambolo_id", insertable = true, updatable = false)
	private SezioneTesto preambolo = new SezioneTesto();

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "motivazione_id", insertable = true, updatable = false)
	private SezioneTesto motivazione = new SezioneTesto();
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "dataadozione_atto_revocato")
	private LocalDate dataAdozioneAttoRevocato;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_pubblicazione_trasparenza")
	private LocalDate dataPubblicazioneTrasparenza;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "inizio_pubblicazione_albo")
	private LocalDate inizioPubblicazioneAlbo;
	
	@Column(name = "prog_pubblicazione_albo")
	private Long progressivoPubblicazioneAlbo;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "fine_pubblicazione_albo")
	private LocalDate finePubblicazioneAlbo;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "inizio_pubblicazione_presunta")
	private LocalDate dataInizioPubblicazionePresunta;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "fine_pubblicazione_presunta")
	private LocalDate dataFinePubblicazionePresunta;

	@Column(name = "stato_pubblicazione")
	private String statoPubblicazione;

	@Column(name = "stato_procedura_pubblicazione")
	private String statoProceduraPubblicazione;

	@Column(name = "stato_relata")
	private Integer statoRelata;

	@Column(name = "oscuramento_atto_pubblicato")
	private Boolean oscuramentoAttoPubblicato = false;

	@Column(name = "motivazione_richiesta_annullamento")
	private String motivazioneRichiestaAnnullamento;

	@Column(name = "richiedente_annullamento")
	private String richiedenteAnnullamento;

	@Column(name = "stato_richiesta_annullamento")
	private String statoRichiestaAnnullamento;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_richiesta_annullamento")
	private LocalDate dataRichiestaAnnullamento;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_adozione")
	private LocalDate dataAdozione;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_numerazione")
	private LocalDate dataNumerazione;

	@Column(name = "numero_adozione")
	private String numeroAdozione;

	@Column(name = "luogo_adozione")
	private String luogoAdozione;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_creazione", insertable = true, updatable = false)
	private LocalDate dataCreazione;

	@Column(name = "luogo_creazione")
	private String luogoCreazione;

	@Column(name = "note")
	private String note;

	@Column(name = "obbligodlgs332013")
	private Boolean obbligodlgs332013;

	@Column(name = "codice_mir")
	private String codiceMir;

	@Column(name = "codice_cig")
	private String codiceCig;

	@Column(name = "codice_cup")
	private String codiceCup;

	@Column(name = "codice_bando")
	private String codiceBando;

	@Column(name = "codice_procedimento")
	private String codiceProcedimento;

	@Column(name = "esito")
	private String esito;

	@Column(name = "votazione")
	private Long votazione;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_pubblica_sito")
	private LocalDate dataPubblicaSito;

	@Column(name = "pubblicazione_sito")
	private Boolean pubblicazioneSito;

	@ManyToOne
	@JoinColumn(insertable = true, updatable = false)
	private Profilo profilo;

	@ManyToOne
	private SottoMateria sottoMateria;

	@ManyToOne
	private Materia materia;

	@ManyToOne
	private TipoMateria tipoMateria;

	@ManyToOne
	@JoinColumn(insertable = true, updatable = false)
	private TipoAtto tipoAtto;

	@Column(name = "atto_revocato_id")
	private Long attoRevocatoId;

	@ManyToOne
	@JoinColumn
	private TipoDeterminazione tipoDeterminazione;

	@ManyToOne
	private ArgomentoOdg argomentoOdg;

	@ManyToOne
	private TipoAdempimento tipoAdempimento;

	@ManyToOne
	@JoinColumn(insertable = true, updatable = false)
	private Aoo aoo;

	@Column(name = "codice_ufficio", insertable = true, updatable = true)
	private String codiceUfficio;

	@Column(name = "descrizione_ufficio", insertable = true, updatable = true)
	private String descrizioneUfficio;

	@Column(name = "codice_servizio", insertable = true, updatable = false)
	private String codiceServizio;

	@Column(name = "descrizione_servizio", insertable = true, updatable = false)
	private String descrizioneServizio;

	@Column(name = "codice_area", insertable = true, updatable = false)
	private String codiceArea;

	@Column(name = "descrizione_area", insertable = true, updatable = false)
	private String descrizioneArea;

	@ManyToOne
	private Ufficio ufficio;

	@ManyToOne
	private TipoIter tipoIter;
	
	@Formula("(select exists(select d.id from DocumentoPdf d where d.atto_adozione_id = id))")
	private Boolean existsProvvedimento;

	@Transient
	@JsonProperty
	private Collection<Object> objs;
	
	@Transient
	@JsonProperty
	private String highLightedInfo;
	
	@Transient
	@JsonProperty
	private SedutaGiunta sedutaGiunta;

	@OneToMany(mappedBy = "atto")
	@OrderBy(value = " ordine_firma ASC")
	private Set<SottoscrittoreAtto> sottoscrittori = new TreeSet<SottoscrittoreAtto>();
	
	@ManyToMany
	@JoinTable(name = "ATTO_PROPONENTI", joinColumns = { @JoinColumn(name = "atto_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "profilo_id", nullable = false) })
	private Set<Profilo> proponenti = new HashSet<Profilo>();
	
	@OneToMany(mappedBy = "atto", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	@OrderBy(value = " ordine_inclusione ASC")
	private Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "ATTO_DESTINATARI", joinColumns = { @JoinColumn(name = "atto_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "destinatario_id", nullable = false) })
	private Set<RubricaDestinatarioEsterno> destinatariEsterni = new HashSet<RubricaDestinatarioEsterno>();

	@OneToMany(mappedBy = "atto", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	private List<AttoHasAmbitoMateria> hasAmbitoMateriaDl = new ArrayList<AttoHasAmbitoMateria>();
	
	@ManyToMany (fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "tipofinanziamento_atto" , joinColumns = { @JoinColumn(name = "atto_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "tipofinanziamento_id", nullable = false, updatable = false) })
    private Set<TipoFinanziamento> hasTipoFinanziamenti = new HashSet<TipoFinanziamento>();
	
	@OneToMany(mappedBy = "atto", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	@OrderBy(value = " scheda_id,progressivo_elemento,scheda_dato_id ASC")
	private Set<AttoSchedaDato> valoriSchedeDati = new HashSet<AttoSchedaDato>();

	@OneToMany(mappedBy = "atto", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	private Set<Beneficiario> beneficiari = new HashSet<Beneficiario>();

	@Column(name = "allegato_beneficiari")
	private Boolean allegatoBeneficiari;

	@ManyToOne
	@JoinColumn(name = "macro_cat_obbligo_dl33_id")
	private Macro_cat_obbligo_dl33 macroCategoriaObbligoDl33;
	
	@OneToOne(mappedBy="atto", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private DatiContabili datiContabili;

	@ManyToOne
	@JoinColumn(name = "cat_obbligo_dl33_id")
	private Cat_obbligo_dl33 categoriaObbligoDl33;

	@ManyToOne
	@JoinColumn(name = "obbligo_dl33_id")
	private Obbligo_DL33 obbligoDl33;

	@OneToMany
	@JoinColumn(name="atto_id", insertable=true, updatable=false)
	@OrderBy(value = "created_date DESC")
	private List<DocumentoPdf> documentiPdf = new ArrayList<DocumentoPdf>();

	@OneToMany
	@JoinColumn(name="atto_scheda_anagrafico_contabile", insertable=true, updatable=false)
	@OrderBy(value = "created_date DESC")
	private List<DocumentoPdf> schedeAnagraficoContabili = new ArrayList<DocumentoPdf>();

	@OneToMany
	@JoinColumn(name="atto_report_iter", insertable=true, updatable=false)
	@OrderBy(value = "created_date DESC")
	private List<DocumentoPdf> reportsIter = new ArrayList<DocumentoPdf>();

	@OneToMany
	@JoinColumn(name="atto_relata_pubblicazione", insertable=true, updatable=false)
	@OrderBy(value = "created_date DESC")
	private List<DocumentoPdf> relatePubblicazione = new ArrayList<DocumentoPdf>();

	@OneToMany
	@JoinColumn(name="atto_adozione_id", insertable=true, updatable=false)
	@OrderBy(value = "created_date DESC")
	private List<DocumentoPdf> documentiPdfAdozione = new ArrayList<DocumentoPdf>();

	@OneToMany
	@JoinColumn(name="atto_omissis_id", insertable=true, updatable=false)
	@OrderBy(value = "created_date DESC")
	private List<DocumentoPdf> documentiPdfOmissis = new ArrayList<DocumentoPdf>();

	@OneToMany
	@JoinColumn(name="atto_adozione_omissis_id", insertable=true, updatable=false)
	@OrderBy(value = "created_date DESC")
	private List<DocumentoPdf> documentiPdfAdozioneOmissis = new ArrayList<DocumentoPdf>();

	@OneToMany(mappedBy = "atto")
	@OrderBy(value = "data_attivita DESC")
	private Set<Avanzamento> avanzamento = new HashSet<Avanzamento>();

	@Column(name = "processo_bpm_id", unique = true)
	private String processoBpmId;

	@OneToMany(mappedBy = "atto")
	@OrderBy(value = "ordine_odg ASC")
	private Set<AttiOdg> ordineGiornos = new HashSet<AttiOdg>();
	
	@Column(name = "pubblicazione_trasparenza_nolimit")
	private Boolean pubblicazioneTrasparenzaNolimit;

	/*
	 * @ManyToOne
	 *
	 * @JoinColumn(name="odg_id") private OrdineGiorno ordineGiorno;
	 *
	 * @Column(name = "ordine_odg") private Integer ordineOdg;
	 */
	@Column(name = "uso_esclusivo")
	private String usoEsclusivo;

	@OneToMany(mappedBy = "atto")
	@OrderBy(value = "created_date DESC")
	private Set<Parere> pareri = new HashSet<Parere>();

	@Column(name = "stato")
	private String stato;
	
	@Transient
	@JsonProperty
	private List taskAttivi;

	@Transient
	@JsonProperty
	private Boolean fullAccess;
	
	@Transient
	@JsonProperty
	private String improntaBozza;
	
	@Transient
	@JsonProperty
	private String improntaAtto;
	
	@Transient
	@JsonProperty
	private Set<AttoHasDestinatario> destinatariInterni = new HashSet<AttoHasDestinatario>();

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_esecutivita", insertable = true, updatable = true)
	private LocalDate dataEsecutivita;

	@Transient
	@JsonProperty
	private LocalDate dataPrecedenteAllaCreazioneAtto;

	@ManyToOne
	@JoinColumn(name = "emanante_profilo_id", insertable = true, updatable = true)
	private Profilo emananteProfilo;

	// ATTICO: non usata
	@ManyToOne
	@JoinColumn(name = "qualifica_emanante_id", insertable = true, updatable = true)
	private QualificaProfessionale qualificaEmanante;
	
	// ATTICO: potrebbe variaire in caso di delega
	@Column(name = "descrizione_qualifica_emanante")
	private String descrizioneQualificaEmanante;

	@ManyToOne
	@JoinColumn(name = "rup_profilo_id", insertable = true, updatable = true)
	private Profilo rupProfilo;

	@Transient
	@JsonProperty
	private String assessoreProponente;
	
	@Transient
	@JsonProperty
	private Long attoclonatoid;

	@Transient
	private Atto attoRevoca;

	@Transient
	@JsonProperty
	private String assenti;

	@Column(name = "modificato_ingiunta", columnDefinition = "tinyint(1) default 0")
	private Boolean isModificatoInGiunta;
	
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_ricevimento")
	private LocalDate dataRicevimento;
	
	@Column(name = "numero_atto_riferimento")
	private String numeroAttoRiferimento;
	
	@Column(name = "tipo_atto_riferimento")
	private String tipoAttoRiferimento;
	
	@Column(name = "fineiter_tipo")
	private String fineiterTipo;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "fineiter_date")
	private LocalDateTime fineIterDate;
	
	// Filtri commissioni
	@Transient
	@JsonProperty
	private Boolean parComNotReq;
	
	@Transient
	@JsonProperty
	private Boolean parComAll;
	
	public String getImprontaBozza() {
		return improntaBozza;
	}

	public void setImprontaBozza(String improntaBozza) {
		this.improntaBozza = improntaBozza;
	}

	public String getImprontaAtto() {
		return improntaAtto;
	}

	public void setImprontaAtto(String improntaAtto) {
		this.improntaAtto = improntaAtto;
	}

	@Transient
	@JsonProperty
	private Boolean parComExpired;
	
	@Transient
	@JsonProperty
	private Boolean editCommissioniEnable;

	
	public String getAssenti() {
		return assenti;
	}

	/*
	 * getter rinominato per impedirne la lettura tramite reflection (beanutils -> copyproperties)
	 */
	public String ottieniIterStatus() {
		return iterStatus;
	}

	public void setIterStatus(String iterStatus) {
		this.iterStatus = iterStatus;
	}
	
	public void setAssenti(String assenti) {
		this.assenti = assenti;
	}

	public Atto getAttoRevoca() {
		return attoRevoca;
	}

	public void setAttoRevoca(Atto attoRevoca) {
		this.attoRevoca = attoRevoca;
	}

	public Boolean getAllegatoBeneficiari() {
		return allegatoBeneficiari;
	}

	public void setAllegatoBeneficiari(Boolean allegatoBeneficiari) {
		this.allegatoBeneficiari = allegatoBeneficiari;
	}

	public Long getAttoclonatoid() {
		return attoclonatoid;
	}

	public void setAttoclonatoid(Long attoclonatoid) {
		this.attoclonatoid = attoclonatoid;
	}
	
	public String getAssessoreProponente() {
		return assessoreProponente;
	}

	public void setAssessoreProponente(String assessoreProponente) {
		this.assessoreProponente = assessoreProponente;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(final String stato) {
		this.stato = stato;
	}

	public List getTaskAttivi() {
		return taskAttivi;
	}

	public void setTaskAttivi(List taskAttivi) {
		this.taskAttivi = taskAttivi;
	}

	public Set<Parere> getPareri() {
		return pareri;
	}

	public void setPareri(final Set<Parere> pareri) {
		this.pareri = pareri;
	}

	public String getProcessoBpmId() {
		return processoBpmId;
	}

	public void setProcessoBpmId(final String processoBpmId) {
		this.processoBpmId = processoBpmId;
	}

	public Boolean getFullAccess() {
		return fullAccess;
	}

	public void setFullAccess(Boolean fullAccess) {
		this.fullAccess = fullAccess;
	}

	public Set<Avanzamento> getAvanzamento() {
		return avanzamento;
	}

	public void setAvanzamento(final Set<Avanzamento> avanzamento) {
		this.avanzamento = avanzamento;
	}

	public Macro_cat_obbligo_dl33 getMacroCategoriaObbligoDl33() {
		return macroCategoriaObbligoDl33;
	}

	public void setMacroCategoriaObbligoDl33(final Macro_cat_obbligo_dl33 macroCategoriaObbligoDl33) {
		this.macroCategoriaObbligoDl33 = macroCategoriaObbligoDl33;
	}

	public Cat_obbligo_dl33 getCategoriaObbligoDl33() {
		return categoriaObbligoDl33;
	}

	public void setCategoriaObbligoDl33(final Cat_obbligo_dl33 categoriaObbligoDl33) {
		this.categoriaObbligoDl33 = categoriaObbligoDl33;
	}

	public Obbligo_DL33 getObbligoDl33() {
		return obbligoDl33;
	}

	public void setObbligoDl33(final Obbligo_DL33 obbligoDl33) {
		this.obbligoDl33 = obbligoDl33;
	}

	public Set<AttoSchedaDato> getValoriSchedeDati() {
		return valoriSchedeDati;
	}

	public void setValoriSchedeDati(final Set<AttoSchedaDato> valoriSchedeDati) {
		this.valoriSchedeDati = valoriSchedeDati;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getRegolamento() {
		return regolamento;
	}

	public void setRegolamento(final String regolamento) {
		this.regolamento = regolamento;
	}

	public String getCodiceCifra() {
		return codiceCifra;
	}

	public String getHighLightedInfo() {
		return highLightedInfo;
	}

	public void setHighLightedInfo(String highLightedInfo) {
		this.highLightedInfo = highLightedInfo;
	}

	public void setCodiceCifra(final String codiceCifra) {
		this.codiceCifra = codiceCifra;
	}

	public String getOggetto() {
		return oggetto;
	}

	public String getCodicecifraAttoProvvisorio() {
		return codicecifraAttoProvvisorio;
	}

	public void setCodicecifraAttoProvvisorio(String codicecifraAttoProvvisorio) {
		this.codicecifraAttoProvvisorio = codicecifraAttoProvvisorio;
	}

	public String getNumeroAdozioneAttoProvvisorio() {
		return numeroAdozioneAttoProvvisorio;
	}

	public void setNumeroAdozioneAttoProvvisorio(String numeroAdozioneAttoProvvisorio) {
		this.numeroAdozioneAttoProvvisorio = numeroAdozioneAttoProvvisorio;
	}

	public LocalDate getDataAdozioneAttoProvvisorio() {
		return dataAdozioneAttoProvvisorio;
	}

	public void setDataAdozioneAttoProvvisorio(LocalDate dataAdozioneAttoProvvisorio) {
		this.dataAdozioneAttoProvvisorio = dataAdozioneAttoProvvisorio;
	}

	public String getNumeroAdozioneAttoRevocato() {
		return numeroAdozioneAttoRevocato;
	}

	public void setNumeroAdozioneAttoRevocato(final String numeroAdozioneAttoRevocato) {
		this.numeroAdozioneAttoRevocato = numeroAdozioneAttoRevocato;
	}

	public LocalDate getDataAdozioneAttoRevocato() {
		return dataAdozioneAttoRevocato;
	}

	public void setDataAdozioneAttoRevocato(final LocalDate dataAdozioneAttoRevocato) {
		this.dataAdozioneAttoRevocato = dataAdozioneAttoRevocato;
	}

	public void setOggetto(final String oggetto) {
		this.oggetto = oggetto;
	}

	public Boolean getRiservato() {
		return riservato;
	}

	public void setRiservato(final Boolean riservato) {
		this.riservato = riservato;
	}

	public Boolean getPubblicazioneIntegrale() {
		return pubblicazioneIntegrale;
	}

	public void setPubblicazioneIntegrale(final Boolean pubblicazioneIntegrale) {
		this.pubblicazioneIntegrale = pubblicazioneIntegrale;
	}

	public Boolean getBurp() {
		return burp;
	}

	public void setBurp(final Boolean burp) {
		this.burp = burp;
	}

	public String getCodicecifraAttoRevocato() {
		return codicecifraAttoRevocato;
	}

	public void setCodicecifraAttoRevocato(final String codicecifraAttoRevocato) {
		this.codicecifraAttoRevocato = codicecifraAttoRevocato;
	}

	public Integer getDurataGiorni() {
		return durataGiorni;
	}

	public void setDurataGiorni(final Integer durataGiorni) {
		this.durataGiorni = durataGiorni;
	}
	
	public LocalDate getDataPubblicazioneTrasparenza() {
		return dataPubblicazioneTrasparenza;
	}

	public Collection<Object> getObjs() {
		return objs;
	}

	public void setObjs(Collection<Object> objs) {
		this.objs = objs;
	}

	public void setDataPubblicazioneTrasparenza(LocalDate dataPubblicazioneTrasparenza) {
		this.dataPubblicazioneTrasparenza = dataPubblicazioneTrasparenza;
	}

	public LocalDate getInizioPubblicazioneAlbo() {
		return inizioPubblicazioneAlbo;
	}

	public void setInizioPubblicazioneAlbo(final LocalDate inizioPubblicazioneAlbo) {
		this.inizioPubblicazioneAlbo = inizioPubblicazioneAlbo;
	}

	public Long getProgressivoPubblicazioneAlbo() {
		return progressivoPubblicazioneAlbo;
	}

	public void setProgressivoPubblicazioneAlbo(Long progressivoPubblicazioneAlbo) {
		this.progressivoPubblicazioneAlbo = progressivoPubblicazioneAlbo;
	}

	public LocalDate getFinePubblicazioneAlbo() {
		return finePubblicazioneAlbo;
	}

	public void setFinePubblicazioneAlbo(final LocalDate finePubblicazioneAlbo) {
		this.finePubblicazioneAlbo = finePubblicazioneAlbo;
	}

	public LocalDate getDataAdozione() {
		return dataAdozione;
	}

	public void setDataAdozione(final LocalDate dataAdozione) {
		this.dataAdozione = dataAdozione;
	}

	public String getNumeroAdozione() {
		return numeroAdozione;
	}

	public void setNumeroAdozione(final String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}

	public String getLuogoAdozione() {
		return luogoAdozione;
	}

	public void setLuogoAdozione(final String luogoAdozione) {
		this.luogoAdozione = luogoAdozione;
	}

	public LocalDate getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(final LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public String getLuogoCreazione() {
		return luogoCreazione;
	}

	public void setLuogoCreazione(final String luogoCreazione) {
		this.luogoCreazione = luogoCreazione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public Boolean getObbligodlgs332013() {
		return obbligodlgs332013;
	}

	public void setObbligodlgs332013(final Boolean obbligodlgs332013) {
		this.obbligodlgs332013 = obbligodlgs332013;
	}

	public String getCodiceMir() {
		return codiceMir;
	}

	public void setCodiceMir(final String codiceMir) {
		this.codiceMir = codiceMir;
	}

	public String getCodiceCig() {
		return codiceCig;
	}

	public void setCodiceCig(final String codiceCig) {
		this.codiceCig = codiceCig;
	}

	public String getCodiceCup() {
		return codiceCup;
	}

	public void setCodiceCup(final String codiceCup) {
		this.codiceCup = codiceCup;
	}

	public String getCodiceBando() {
		return codiceBando;
	}

	public void setCodiceBando(final String codiceBando) {
		this.codiceBando = codiceBando;
	}

	public String getCodiceProcedimento() {
		return codiceProcedimento;
	}

	public void setCodiceProcedimento(final String codiceProcedimento) {
		this.codiceProcedimento = codiceProcedimento;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(final String esito) {
		this.esito = esito;
	}

	public Long getVotazione() {
		return votazione;
	}

	public void setVotazione(final Long votazione) {
		this.votazione = votazione;
	}

	public LocalDate getDataPubblicaSito() {
		return dataPubblicaSito;
	}

	public void setDataPubblicaSito(final LocalDate dataPubblicaSito) {
		this.dataPubblicaSito = dataPubblicaSito;
	}

	public Boolean getPubblicazioneSito() {
		return pubblicazioneSito;
	}

	public void setPubblicazioneSito(final Boolean pubblicazioneSito) {
		this.pubblicazioneSito = pubblicazioneSito;
	}

	public Profilo getProfilo() {
		return profilo;
	}

	public void setProfilo(final Profilo profilo) {
		this.profilo = profilo;
	}

	public SottoMateria getSottoMateria() {
		return sottoMateria;
	}

	public void setSottoMateria(final SottoMateria sottoMateria) {
		this.sottoMateria = sottoMateria;
	}

	public Materia getMateria() {
		return materia;
	}

	public void setMateria(final Materia materia) {
		this.materia = materia;
	}

	public TipoMateria getTipoMateria() {
		return tipoMateria;
	}

	public void setTipoMateria(final TipoMateria tipoMateria) {
		this.tipoMateria = tipoMateria;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAtto(final TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public TipoDeterminazione getTipoDeterminazione() {
		return tipoDeterminazione;
	}

	public void setTipoDeterminazione(final TipoDeterminazione tipoDeterminazione) {
		this.tipoDeterminazione = tipoDeterminazione;
	}

	public ArgomentoOdg getArgomentoOdg() {
		return argomentoOdg;
	}

	public void setArgomentoOdg(final ArgomentoOdg argomentoOdg) {
		this.argomentoOdg = argomentoOdg;
	}

	public TipoAdempimento getTipoAdempimento() {
		return tipoAdempimento;
	}

	public void setTipoAdempimento(final TipoAdempimento tipoAdempimento) {
		this.tipoAdempimento = tipoAdempimento;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(final Aoo aoo) {
		this.aoo = aoo;
	}

	public Ufficio getUfficio() {
		return ufficio;
	}

	public void setUfficio(final Ufficio ufficio) {
		this.ufficio = ufficio;
	}

	public TipoIter getTipoIter() {
		return tipoIter;
	}

	public void setTipoIter(final TipoIter tipoIter) {
		this.tipoIter = tipoIter;
	}

	public SedutaGiunta getSedutaGiunta() {
		return sedutaGiunta;
	}

	public void setSedutaGiunta(final SedutaGiunta sedutaGiunta) {
		this.sedutaGiunta = sedutaGiunta;
	}

	public Set<SottoscrittoreAtto> getSottoscrittori() {
		return sottoscrittori;
	}

	public void setSottoscrittori(final Set<SottoscrittoreAtto> sottoscrittori) {
		this.sottoscrittori = sottoscrittori;
	}

	public Set<DocumentoInformatico> getAllegati() {
		return allegati;
	}

	public void setAllegati(final Set<DocumentoInformatico> allegati) {
		this.allegati = allegati;
	}

	public String getCodiceUfficio() {
		return codiceUfficio;
	}

	public void setCodiceUfficio(final String codiceUfficio) {
		this.codiceUfficio = codiceUfficio;
	}

	public String getDescrizioneUfficio() {
		return descrizioneUfficio;
	}

	public void setDescrizioneUfficio(final String descrizioneUfficio) {
		this.descrizioneUfficio = descrizioneUfficio;
	}

	public String getCodiceServizio() {
		return codiceServizio;
	}

	public void setCodiceServizio(final String codiceServizio) {
		this.codiceServizio = codiceServizio;
	}

	public String getDescrizioneServizio() {
		return descrizioneServizio;
	}

	public void setDescrizioneServizio(final String descrizioneServizio) {
		this.descrizioneServizio = descrizioneServizio;
	}

	public String getCodiceArea() {
		return codiceArea;
	}

	public void setCodiceArea(final String codiceArea) {
		this.codiceArea = codiceArea;
	}

	public String getDescrizioneArea() {
		return descrizioneArea;
	}

	public void setDescrizioneArea(final String descrizioneArea) {
		this.descrizioneArea = descrizioneArea;
	}

	public Set<RubricaDestinatarioEsterno> getDestinatariEsterni() {
		return destinatariEsterni;
	}

	public void setDestinatariEsterni(final Set<RubricaDestinatarioEsterno> destinatariEsterni) {
		this.destinatariEsterni = destinatariEsterni;
	}

	public Set<Profilo> getProponenti() {
		return proponenti;
	}

	public Boolean getParComNotReq() {
		return parComNotReq;
	}

	public void setParComNotReq(Boolean parComNotReq) {
		this.parComNotReq = parComNotReq;
	}

	public Boolean getParComAll() {
		return parComAll;
	}

	public void setParComAll(Boolean parComAll) {
		this.parComAll = parComAll;
	}

	public Boolean getParComExpired() {
		return parComExpired;
	}

	public void setParComExpired(Boolean parComExpired) {
		this.parComExpired = parComExpired;
	}

	public void setProponenti(Set<Profilo> proponenti) {
		this.proponenti = proponenti;
	}

	public List<AttoHasAmbitoMateria> getHasAmbitoMateriaDl() {
		return hasAmbitoMateriaDl;
	}

	public void setHasAmbitoMateriaDl(final List<AttoHasAmbitoMateria> hasAmbitoMateriaDl) {
		this.hasAmbitoMateriaDl = hasAmbitoMateriaDl;
	}

	public String getInfoattodelega() {
		return infoattodelega;
	}

	public Set<TipoFinanziamento> getHasTipoFinanziamenti() {
		return hasTipoFinanziamenti;
	}

	public void setHasTipoFinanziamenti(Set<TipoFinanziamento> hasTipoFinanziamenti) {
		this.hasTipoFinanziamenti = hasTipoFinanziamenti;
	}

	public void setInfoattodelega(final String infoattodelega) {
		this.infoattodelega = infoattodelega;
	}

	public SezioneTesto getGaranzieRiservatezza() {
		return garanzieRiservatezza;
	}

	public void setGaranzieRiservatezza(final SezioneTesto garanzieRiservatezza) {
		this.garanzieRiservatezza = garanzieRiservatezza;
	}

	public SezioneTesto getDichiarazioni() {
		return dichiarazioni;
	}

	public void setDichiarazioni(final SezioneTesto dichiarazioni) {
		this.dichiarazioni = dichiarazioni;
	}

	public SezioneTesto getInformazioniAnagraficoContabili() {
		return informazioniAnagraficoContabili;
	}

	public void setInformazioniAnagraficoContabili(final SezioneTesto informazioniAnagraficoContabili) {
		this.informazioniAnagraficoContabili = informazioniAnagraficoContabili;
	}

	public SezioneTesto getDispositivo() {
		return dispositivo;
	}

	public void setDispositivo(final SezioneTesto dispositivo) {
		this.dispositivo = dispositivo;
	}

	public SezioneTesto getDomanda() {
		return domanda;
	}

	public void setDomanda(SezioneTesto domanda) {
		this.domanda = domanda;
	}

	public SezioneTesto getAdempimentiContabili() {
		return adempimentiContabili;
	}

	public void setAdempimentiContabili(final SezioneTesto adempimentiContabili) {
		this.adempimentiContabili = adempimentiContabili;
	}

	public SezioneTesto getNoteMotivazione() {
		return noteMotivazione;
	}

	public void setNoteMotivazione(final SezioneTesto noteMotivazione) {
		this.noteMotivazione = noteMotivazione;
	}

	public SezioneTesto getPreambolo() {
		return preambolo;
	}

	public void setPreambolo(final SezioneTesto preambolo) {
		this.preambolo = preambolo;
	}

	public SezioneTesto getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(final SezioneTesto motivazione) {
		this.motivazione = motivazione;
	}

	public List<DocumentoPdf> getDocumentiPdf() {
		return documentiPdf;
	}

	public void setDocumentiPdf(final List<DocumentoPdf> documentiPdf) {
		this.documentiPdf = documentiPdf;
	}

	public List<DocumentoPdf> getDocumentiPdfAdozione() {
		return documentiPdfAdozione;
	}

	public void setDocumentiPdfAdozione(final List<DocumentoPdf> documentiPdfAdozione) {
		this.documentiPdfAdozione = documentiPdfAdozione;
	}

	public List<DocumentoPdf> getDocumentiPdfOmissis() {
		return documentiPdfOmissis;
	}

	public void setDocumentiPdfOmissis(final List<DocumentoPdf> documentiPdfOmissis) {
		this.documentiPdfOmissis = documentiPdfOmissis;
	}

	public List<DocumentoPdf> getDocumentiPdfAdozioneOmissis() {
		return documentiPdfAdozioneOmissis;
	}

	public void setDocumentiPdfAdozioneOmissis(final List<DocumentoPdf> documentiPdfAdozioneOmissis) {
		this.documentiPdfAdozioneOmissis = documentiPdfAdozioneOmissis;
	}

	public Set<AttoHasDestinatario> getDestinatariInterni() {
		return destinatariInterni;
	}

	public void setDestinatariInterni(final Set<AttoHasDestinatario> destinatariInterni) {
		this.destinatariInterni = destinatariInterni;
	}

	public Profilo getEmananteProfilo() {
		return emananteProfilo;
	}

	public void setEmananteProfilo(Profilo emananteProfilo) {
		this.emananteProfilo = emananteProfilo;
	}

	public QualificaProfessionale getQualificaEmanante() {
		return qualificaEmanante;
	}

	public void setQualificaEmanante(QualificaProfessionale qualificaEmanante) {
		this.qualificaEmanante = qualificaEmanante;
	}

	public Profilo getRupProfilo() {
		return rupProfilo;
	}

	public void setRupProfilo(Profilo rupProfilo) {
		this.rupProfilo = rupProfilo;
	}

	public Set<Beneficiario> getBeneficiari() {
		return beneficiari;
	}

	public void setBeneficiari(Set<Beneficiario> beneficiari) {
		this.beneficiari = beneficiari;
	}

	public String getMotivoClonazione() {
		return motivoClonazione;
	}

	public void setMotivoClonazione(String motivoClonazione) {
		this.motivoClonazione = motivoClonazione;
	}

	public List<DocumentoPdf> getSchedeAnagraficoContabili() {
		return schedeAnagraficoContabili;
	}

	public void setSchedeAnagraficoContabili(List<DocumentoPdf> schedeAnagraficoContabili) {
		this.schedeAnagraficoContabili = schedeAnagraficoContabili;
	}

	public Boolean getCongiunto() {
		return congiunto;
	}

	public void setCongiunto(Boolean congiunto) {
		this.congiunto = congiunto;
	}

	public Set<AttiOdg> getOrdineGiornos() {
		return ordineGiornos;
	}

	public void setOrdineGiornos(Set<AttiOdg> ordineGiornos) {
		this.ordineGiornos = ordineGiornos;
	}

	public String getUsoEsclusivo() {
		return usoEsclusivo;
	}

	public void setUsoEsclusivo(String usoEsclusivo) {
		this.usoEsclusivo = usoEsclusivo;
	}

	public Boolean getNotificaBeneficiari() {
		if (notificaBeneficiari == null) {
			return true;
		}
		else {
			return notificaBeneficiari;
		}
	}

	public void setNotificaBeneficiari(Boolean notificaBeneficiari) {
		this.notificaBeneficiari = notificaBeneficiari;
	}

	public Set<Nota> getListaNote() {
		return listaNote;
	}

	public void setListaNote(Set<Nota> listaNote) {
		this.listaNote = listaNote;
	}

	public List<DocumentoPdf> getRelatePubblicazione() {
		return relatePubblicazione;
	}

	public void setRelatePubblicazione(List<DocumentoPdf> relatePubblicazione) {
		this.relatePubblicazione = relatePubblicazione;
	}

	public LocalDate getDataInizioPubblicazionePresunta() {
		return dataInizioPubblicazionePresunta;
	}

	public LocalDate getDataFinePubblicazionePresunta() {
		return dataFinePubblicazionePresunta;
	}

	public void setDataInizioPubblicazionePresunta(LocalDate dataInizioPubblicazionePresunta) {
		this.dataInizioPubblicazionePresunta = dataInizioPubblicazionePresunta;
	}

	public void setDataFinePubblicazionePresunta(LocalDate dataFinePubblicazionePresunta) {
		this.dataFinePubblicazionePresunta = dataFinePubblicazionePresunta;
	}

	public String getStatoPubblicazione() {
		return statoPubblicazione;
	}

	public void setStatoPubblicazione(String statoPubblicazione) {
		this.statoPubblicazione = statoPubblicazione;
	}

	public String getStatoProceduraPubblicazione() {
		return statoProceduraPubblicazione;
	}

	public void setStatoProceduraPubblicazione(String statoProceduraPubblicazione) {
		this.statoProceduraPubblicazione = statoProceduraPubblicazione;
	}

	public Integer getStatoRelata() {
		return statoRelata;
	}

	public void setStatoRelata(Integer statoRelata) {
		this.statoRelata = statoRelata;
	}

	public Boolean getIsModificatoInGiunta() {
		return isModificatoInGiunta;
	}

	public void setIsModificatoInGiunta(Boolean isModificatoInGiunta) {
		this.isModificatoInGiunta = isModificatoInGiunta;
	}

	public Boolean getOscuramentoAttoPubblicato() {
		return oscuramentoAttoPubblicato;
	}

	public void setOscuramentoAttoPubblicato(Boolean oscuramentoAttoPubblicato) {
		this.oscuramentoAttoPubblicato = oscuramentoAttoPubblicato;
	}

	public String getMotivazioneRichiestaAnnullamento() {
		return motivazioneRichiestaAnnullamento;
	}

	public void setMotivazioneRichiestaAnnullamento(String motivazioneRichiestaAnnullamento) {
		this.motivazioneRichiestaAnnullamento = motivazioneRichiestaAnnullamento;
	}

	public String getRichiedenteAnnullamento() {
		return richiedenteAnnullamento;
	}

	public void setRichiedenteAnnullamento(String richiedenteAnnullamento) {
		this.richiedenteAnnullamento = richiedenteAnnullamento;
	}

	public String getStatoRichiestaAnnullamento() {
		return statoRichiestaAnnullamento;
	}

	public void setStatoRichiestaAnnullamento(String statoRichiestaAnnullamento) {
		this.statoRichiestaAnnullamento = statoRichiestaAnnullamento;
	}

	public LocalDate getDataRichiestaAnnullamento() {
		return dataRichiestaAnnullamento;
	}

	public void setDataRichiestaAnnullamento(LocalDate dataRichiestaAnnullamento) {
		this.dataRichiestaAnnullamento = dataRichiestaAnnullamento;
	}

	public String getDataPrecedenteAllaCreazioneAtto() {
		String data = null;
		if (this.dataCreazione != null) {
			Calendar c = new GregorianCalendar();
			c.setTime(this.dataCreazione.toDate());
			c.add(Calendar.DATE, -1);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			data = df.format(c.getTime());
		}
		return data;
	}

	public List<DocumentoPdf> getReportsIter() {
		return reportsIter;
	}

	public void setReportsIter(List<DocumentoPdf> reportsIter) {
		this.reportsIter = reportsIter;
	}

	public Long getAttoRevocatoId() {
		return attoRevocatoId;
	}

	public void setAttoRevocatoId(Long attoRevocatoId) {
		this.attoRevocatoId = attoRevocatoId;
	}

	public Boolean getOblio() {
		return oblio;
	}

	public void setOblio(Boolean oblio) {
		this.oblio = oblio;
	}

	public String getContabileOggetto() {
		return contabileOggetto;
	}

	public void setContabileOggetto(String contabileOggetto) {
		this.contabileOggetto = contabileOggetto;
	}

	public void setDataPrecedenteAllaCreazioneAtto(LocalDate dataPrecedenteAllaCreazioneAtto) {
		this.dataPrecedenteAllaCreazioneAtto = dataPrecedenteAllaCreazioneAtto;
	}

	/**
	 * @return the dataEsecutivita
	 */
	public LocalDate getDataEsecutivita() {
		return dataEsecutivita;
	}

	/**
	 * @param dataEsecutivita the dataEsecutivita to set
	 */
	public void setDataEsecutivita(LocalDate dataEsecutivita) {
		this.dataEsecutivita = dataEsecutivita;
	}

	public String getDescrizioneQualificaEmanante() {
		return descrizioneQualificaEmanante;
	}

	public void setDescrizioneQualificaEmanante(String descrizioneQualificaEmanante) {
		this.descrizioneQualificaEmanante = descrizioneQualificaEmanante;
	}

	public LocalDate getDataRicevimento() {
		return dataRicevimento;
	}

	public void setDataRicevimento(LocalDate dataRicevimento) {
		this.dataRicevimento = dataRicevimento;
	}

	public String getNumeroAttoRiferimento() {
		return numeroAttoRiferimento;
	}

	public void setNumeroAttoRiferimento(String numeroAttoRiferimento) {
		this.numeroAttoRiferimento = numeroAttoRiferimento;
	}

	public String getTipoAttoRiferimento() {
		return tipoAttoRiferimento;
	}

	public void setTipoAttoRiferimento(String tipoAttoRiferimento) {
		this.tipoAttoRiferimento = tipoAttoRiferimento;
	}
	

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Atto atto = (Atto) o;

		if (!Objects.equals(id, atto.id)) {
			return false;
		}

		return true;
	}
	
	public Boolean getPubblicazioneTrasparenzaNolimit() {
		return pubblicazioneTrasparenzaNolimit;
	}

	public void setPubblicazioneTrasparenzaNolimit(Boolean pubblicazioneTrasparenzaNolimit) {
		this.pubblicazioneTrasparenzaNolimit = pubblicazioneTrasparenzaNolimit;
	}

	public String getFineiterTipo() {
		return fineiterTipo;
	}

	public void setFineiterTipo(String fineiterTipo) {
		this.fineiterTipo = fineiterTipo;
	}

	public LocalDateTime getFineIterDate() {
		return fineIterDate;
	}

	public void setFineIterDate(LocalDateTime fineIterDate) {
		this.fineIterDate = fineIterDate;
	}

	public DatiContabili getDatiContabili() {
		return datiContabili;
	}

	public void setDatiContabili(DatiContabili datiContabili) {
		this.datiContabili = datiContabili;
	}

	public Boolean getIe() {
		return ie;
	}

	public void setIe(Boolean ie) {
		this.ie = ie;
	}

	public Boolean getExistsProvvedimento() {
		return existsProvvedimento;
	}
	
	public LocalDate getDataNumerazione() {
		return dataNumerazione;
	}

	public void setDataNumerazione(LocalDate dataNumerazione) {
		this.dataNumerazione = dataNumerazione;
	}

	public void setExistsProvvedimento(Boolean existsProvvedimento) {
		this.existsProvvedimento = existsProvvedimento;
	}

	@Override
	public int compareTo(Atto o) {
		return this.codiceCifra.compareToIgnoreCase(o.getCodiceCifra());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Atto [id=" + id + ", codiceCifra=" + codiceCifra + ", oggetto=" + oggetto + ", stato=" + stato + ", riservato=" + riservato + ", pubblicazioneIntegrale=" + pubblicazioneIntegrale
				+ ", codicecifraAttoRevocato=" + codicecifraAttoRevocato + ", dataCreazione=" + dataCreazione + ", luogoCreazione=" + luogoCreazione + ", aoo=" + aoo + "]";
	}

	public Boolean getEditCommissioniEnable() {
		return editCommissioniEnable;
	}

	public void setEditCommissioniEnable(Boolean editCommissioniEnable) {
		this.editCommissioniEnable = editCommissioniEnable;
	}

}
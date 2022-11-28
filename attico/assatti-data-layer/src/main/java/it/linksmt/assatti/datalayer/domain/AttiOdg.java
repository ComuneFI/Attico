package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

/**
 * A AttiOdg.
 */
@Entity
@Table(name = "ATTIODG" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AttiOdg extends AbstractAuditingEntity implements Serializable, Comparable<AttiOdg> {

	private static final long serialVersionUID = 1L;
	
	public AttiOdg() {	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "esito")
	private String esito;
	
	@Column(name = "approvata_ie")
    private Boolean approvataIE;
	
	@Column(name = "sezione")
	private Integer sezione;
	
	@Column(name = "parte")
	private Integer parte;
	
	@ManyToOne
	@JoinColumn(name="odg_id")
    private OrdineGiorno ordineGiorno;
	
	@ManyToOne
	@JoinColumn(name="atto_id")
    private Atto atto;
	
	@ManyToOne
	@JoinColumn(name="id_composizione")
    private Composizione composizione;
	
	@Column(name = "ordine_odg")
    private Integer ordineOdg;
	
	@Column(name = "numero_discussione")
    private Integer numeroDiscussione;
	
	@Column(name = "numero_argomento")
    private Integer numeroArgomento;
	
	@Column(name = "narg_ode")
	private Boolean nargOde;
	
	@Column(name = "votazione_segreta")
    private Boolean votazioneSegreta;
	
	@Column(name = "votazione_ie")
    private Boolean votazioneIE;
	
	@Column(name = "dichiarazioni_voto")
    private String dichiarazioniVoto;
	
	@Column(name = "num_presenti")
    private Integer numPresenti;
	
	@Column(name = "num_assenti")
    private Integer numAssenti;
	
	@Column(name = "num_favorevoli")
    private Integer numFavorevoli;
	
	@Column(name = "num_contrari")
    private Integer numContrari;
	
	@Column(name = "num_astenuti")
    private Integer numAstenuti;
	
	@Column(name = "num_npv")
    private Integer numNpv;
	
	@Column(name = "seduta_convocata")
    private String sedutaConvocata;
	
	@Column(name = "atto_presentato")
	private String attoPresentato;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "data_conferma_esito")
	private LocalDateTime dataConfermaEsito;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_discussione")
	private LocalDate dataDiscussione;
	
	@Column(name = "blocco_modifica")
    private Boolean bloccoModifica;	
	
	@OneToMany(mappedBy = "atto")	
	private List<ComponentiGiunta> componenti = new ArrayList<ComponentiGiunta>();
	
	@Transient
	@JsonProperty
	private Boolean argomentoExSeduta;
	
	@Transient
	@JsonProperty
	private String nextTaskId;
	
	@Transient
	@JsonProperty
	private boolean ultimoOdg;
	
	@Transient
	@JsonProperty
	private boolean numerabile;
	
	@Transient
	@JsonProperty
	private List<String> assenti;
	
	@Transient
	@JsonProperty
	private String presidente;
	
	@Transient
	@JsonProperty
	private String segretario;
	
	@Transient
	@JsonProperty
	private Long presidenteid;
	
	@Transient
	@JsonProperty
	private Long segretarioid;
	
	@Transient
	@JsonProperty
	private String esitoLabel;
		
	public String getPresidente() {
		return presidente;
	}

	public void setPresidente(String presidente) {
		this.presidente = presidente;
	}

	public String getSegretario() {
		return segretario;
	}

	public void setSegretario(String segretario) {
		this.segretario = segretario;
	}
	
	public Long getPresidenteid() {
		return presidenteid;
	}

	public void setPresidenteid(Long presidenteid) {
		this.presidenteid = presidenteid;
	}

	public Long getSegretarioid() {
		return segretarioid;
	}

	public void setSegretarioid(Long segretarioid) {
		this.segretarioid = segretarioid;
	}

	public String getEsitoLabel() {
		return esitoLabel;
	}

	public void setEsitoLabel(String esitoLabel) {
		this.esitoLabel = esitoLabel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}
	public Boolean getApprovataIE() {
		return approvataIE;
	}

	public void setApprovataIE(Boolean approvataIE) {
		this.approvataIE = approvataIE;
	}

	public OrdineGiorno getOrdineGiorno() {
		return ordineGiorno;
	}

	public void setOrdineGiorno(OrdineGiorno ordineGiorno) {
		this.ordineGiorno = ordineGiorno;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public Integer getOrdineOdg() {
		return ordineOdg;
	}

	public void setOrdineOdg(Integer ordineOdg) {
		this.ordineOdg = ordineOdg;
	}
	
	public Integer getNumeroDiscussione() {
		return numeroDiscussione;
	}

	public void setNumeroDiscussione(Integer numeroDiscussione) {
		this.numeroDiscussione = numeroDiscussione;
	}

	public Integer getNumeroArgomento() {
		return numeroArgomento;
	}

	public void setNumeroArgomento(Integer numeroArgomento) {
		this.numeroArgomento = numeroArgomento;
	}

	public Integer getSezione() {
		return sezione;
	}

	public void setSezione(Integer sezione) {
		this.sezione = sezione;
	}

	public Integer getParte() {
		return parte;
	}

	public void setParte(Integer parte) {
		this.parte = parte;
	}	
	
	public Boolean getVotazioneSegreta() {
		return votazioneSegreta;
	}

	public void setVotazioneSegreta(Boolean votazioneSegreta) {
		this.votazioneSegreta = votazioneSegreta;
	}

	public Boolean getVotazioneIE() {
		return votazioneIE;
	}

	public void setVotazioneIE(Boolean votazioneIE) {
		this.votazioneIE = votazioneIE;
	}

	public String getDichiarazioniVoto() {
		return dichiarazioniVoto;
	}

	public void setDichiarazioniVoto(String dichiarazioniVoto) {
		this.dichiarazioniVoto = dichiarazioniVoto;
	}

	public Integer getNumPresenti() {
		return numPresenti;
	}

	public void setNumPresenti(Integer numPresenti) {
		this.numPresenti = numPresenti;
	}

	public Integer getNumAssenti() {
		return numAssenti;
	}

	public void setNumAssenti(Integer numAssenti) {
		this.numAssenti = numAssenti;
	}

	public Integer getNumFavorevoli() {
		return numFavorevoli;
	}

	public void setNumFavorevoli(Integer numFavorevoli) {
		this.numFavorevoli = numFavorevoli;
	}

	public Integer getNumContrari() {
		return numContrari;
	}

	public void setNumContrari(Integer numContrari) {
		this.numContrari = numContrari;
	}

	public Integer getNumAstenuti() {
		return numAstenuti;
	}

	public void setNumAstenuti(Integer numAstenuti) {
		this.numAstenuti = numAstenuti;
	}

	public Integer getNumNpv() {
		return numNpv;
	}

	public void setNumNpv(Integer numNpv) {
		this.numNpv = numNpv;
	}
	
	public String getSedutaConvocata() {
		return sedutaConvocata;
	}

	public void setSedutaConvocata(String sedutaConvocata) {
		this.sedutaConvocata = sedutaConvocata;
	}

	public String getAttoPresentato() {
		return attoPresentato;
	}

	public void setAttoPresentato(String attoPresentato) {
		this.attoPresentato = attoPresentato;
	}

	public List<ComponentiGiunta> getComponenti() {
		return componenti;
	}

	public void setComponenti(List<ComponentiGiunta> componenti) {
		this.componenti = componenti;
	}
	
	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public boolean isNumerabile() {
		return numerabile;
	}

	public void setNumerabile(boolean numerabile) {
		this.numerabile = numerabile;
	}

	public boolean isUltimoOdg() {
		return ultimoOdg;
	}

	public void setUltimoOdg(boolean ultimoOdg) {
		this.ultimoOdg = ultimoOdg;
	}

	public List<String> getAssenti() {
		return this.assenti;
	}
	
	public void setAssenti(List<String> assenti) {
		this.assenti = assenti;
	}

	public LocalDate getDataDiscussione() {
		return dataDiscussione;
	}

	public void setDataDiscussione(LocalDate dataDiscussione) {
		this.dataDiscussione = dataDiscussione;
	}
	
	public Boolean getBloccoModifica() {
		return bloccoModifica;
	}

	public void setBloccoModifica(Boolean bloccoModifica) {
		this.bloccoModifica = bloccoModifica;
	}

	public LocalDateTime getDataConfermaEsito() {
		return dataConfermaEsito;
	}

	public void setDataConfermaEsito(LocalDateTime dataConfermaEsito) {
		this.dataConfermaEsito = dataConfermaEsito;
	}

	public Boolean getArgomentoExSeduta() {
		return argomentoExSeduta;
	}

	public void setArgomentoExSeduta(Boolean argomentoExSeduta) {
		this.argomentoExSeduta = argomentoExSeduta;
	}

	public Boolean getNargOde() {
		return nargOde;
	}

	public void setNargOde(Boolean nargOde) {
		this.nargOde = nargOde;
	}
	
	public Composizione getComposizione() {
		return composizione;
	}

	public void setComposizione(Composizione composizione) {
		this.composizione = composizione;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AttiOdg atto = (AttiOdg) o;

		if (!Objects.equals(id, atto.id)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override
	public int compareTo(AttiOdg o) {
		if(!this.atto.getAoo().getId().equals(o.getAtto().getAoo().getId())){
			return this.atto.getAoo().getDescrizione().compareToIgnoreCase(o.getAtto().getAoo().getDescrizione());
		}else{
			return this.atto.getCodiceCifra().compareToIgnoreCase(o.getAtto().getCodiceCifra());
		}
	}

	@Override
	public String toString() {
		return "Atto [id=" + id + ", odg=" + ordineGiorno + ", atto=" + atto +  "]";
	}

}

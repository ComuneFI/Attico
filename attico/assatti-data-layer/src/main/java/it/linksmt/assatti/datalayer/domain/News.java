package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A News.
 */
@Entity
@Table(name = "NEWS")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	    
public class News implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 542127035709640154L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name="dettaglio_errore")
	private String dettaglioErrore;

	@Enumerated(EnumType.STRING)
	@Column(name="stato")
	private StatoJob stato;
	
	@Column(name="oggetto")
	private String oggetto;
	
	/**
	 * Indirizzo Pec o e-mail del destinatario della notifica
	 */
	@Column(name="destinazione_notifica")
	private String destinazioneNotifica;
	
	@ManyToOne
	@JoinColumn(name="retry_news_id", insertable = true, updatable = false)
    private News originalNews;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "data_creazione", nullable=false)
    private DateTime dataCreazione;
	
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "data_invio", nullable=true)
    private DateTime dataInvio;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "data_start_invio", nullable=true)
    private DateTime dataStartInvio;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "data_errore", nullable=true)
    private DateTime dataErrore;

    @ManyToOne
	@JoinColumn(name="atto_id", insertable = true, updatable = false)
    private Atto atto;
    
    @Column(name = "dest_esterno_id")
    private Long destinatarioEsternoId;
    
    @ManyToOne
    @JoinColumn(name = "dest_interno_id", insertable = true, updatable = false)
    private AttoHasDestinatario destinatarioInterno;
    
    @ManyToOne
    @JoinColumn(name = "beneficiario_id", insertable = true, updatable = false)
    private Beneficiario beneficiario;

    @ManyToOne
	@JoinColumn(name="autore_id", insertable = true, updatable = false)
    private Profilo autore;
    
    /**
     * Colonne aggiunte per la gestione delle notifiche sull' Ordine del Giorno
     */
    @ManyToOne
	@JoinColumn(name="documento_id", insertable = true, updatable = true)
    private DocumentoPdf documento;
    
    @Column(name="tipo_invio")
    private String tipoInvio;
    
    @Column(name="progressivo_tentativo")
    private int progressivoTentativo;
    
    @Enumerated(EnumType.STRING)
	@Column(name="tipo_documento")
    private TipoDocumentoNotificaEnum tipoDocumento;
    @Transient
    private String nominativoDestinatario;
    @Transient
    private String mailDestinatario;
    
    @JsonProperty
    @Transient
    private Long tentativi;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DateTime getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(DateTime dataInvio) {
		this.dataInvio = dataInvio;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public Long getDestinatarioEsternoId() {
		return destinatarioEsternoId;
	}

	public void setDestinatarioEsternoId(Long destinatarioEsternoId) {
		this.destinatarioEsternoId = destinatarioEsternoId;
	}

	public AttoHasDestinatario getDestinatarioInterno() {
		return destinatarioInterno;
	}

	public void setDestinatarioInterno(AttoHasDestinatario destinatarioInterno) {
		this.destinatarioInterno = destinatarioInterno;
	}

	public Profilo getAutore() {
		return autore;
	}

	public void setAutore(Profilo autore) {
		this.autore = autore;
	}

	public Beneficiario getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(Beneficiario beneficiario) {
		this.beneficiario = beneficiario;
	}
	
	public DocumentoPdf getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentoPdf documento) {
		this.documento = documento;
	}

	public String getTipoInvio() {
		return tipoInvio;
	}

	public void setTipoInvio(String tipoInvio) {
		this.tipoInvio = tipoInvio;
	}

	public TipoDocumentoNotificaEnum getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumentoNotificaEnum tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	
	public String getNominativoDestinatario() {
		return nominativoDestinatario;
	}

	public void setNominativoDestinatario(String nominativoDestinatario) {
		this.nominativoDestinatario = nominativoDestinatario;
	}

	public String getMailDestinatario() {
		return mailDestinatario;
	}

	public void setMailDestinatario(String mailDestinatario) {
		this.mailDestinatario = mailDestinatario;
	}

	public StatoJob getStato() {
		return stato;
	}

	public void setStato(StatoJob stato) {
		this.stato = stato;
	}

	public DateTime getDataStartInvio() {
		return dataStartInvio;
	}

	public void setDataStartInvio(DateTime dataStartInvio) {
		this.dataStartInvio = dataStartInvio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atto == null) ? 0 : atto.hashCode());
		result = prime * result + ((documento == null) ? 0 : documento.hashCode());
		result = prime * result + ((autore == null) ? 0 : autore.hashCode());
		result = prime * result
				+ ((dataInvio == null) ? 0 : dataInvio.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public DateTime getDataErrore() {
		return dataErrore;
	}

	public String getDettaglioErrore() {
		return dettaglioErrore;
	}

	public void setDettaglioErrore(String dettaglioErrore) {
		this.dettaglioErrore = dettaglioErrore;
	}

	public void setDataErrore(DateTime dataErrore) {
		this.dataErrore = dataErrore;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		News other = (News) obj;
		if (atto == null) {
			if (other.atto != null)
				return false;
		} else if (!atto.equals(other.atto))
			return false;
		if (documento == null) {
			if (other.documento != null)
				return false;
		} else if (!documento.equals(other.documento))
			return false;
		if (autore == null) {
			if (other.autore != null)
				return false;
		} else if (!autore.equals(other.autore))
			return false;
		if (dataInvio == null) {
			if (other.dataInvio != null)
				return false;
		} else if (!dataInvio.equals(other.dataInvio))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (tipoInvio == null) {
			if (other.tipoInvio != null)
				return false;
		} else if (!tipoInvio.equals(other.tipoInvio))
			return false;
		return true;
	}

	public News getOriginalNews() {
		return originalNews;
	}

	public DateTime getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(DateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public void setOriginalNews(News originalNews) {
		this.originalNews = originalNews;
	}

	public Long getTentativi() {
		return tentativi;
	}

	public void setTentativi(Long tentativi) {
		this.tentativi = tentativi;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getDestinazioneNotifica() {
		return destinazioneNotifica;
	}

	public void setDestinazioneNotifica(String destinazioneNotifica) {
		this.destinazioneNotifica = destinazioneNotifica;
	}

	public int getProgressivoTentativo() {
		return progressivoTentativo;
	}

	public void setProgressivoTentativo(int progressivoTentativo) {
		this.progressivoTentativo = progressivoTentativo;
	}	
	
	
}

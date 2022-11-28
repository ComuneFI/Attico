package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

/**
 * A DocumentoInformatico.
 */
@Entity
@Table(name = "DOCUMENTOINFORMATICO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DocumentoInformatico implements Serializable, ICmisDoc {

	private static final long serialVersionUID = 1L;


	public DocumentoInformatico(  File file, Atto atto, Integer ordineInclusione ) {
		super();
		this.nomeFile = file.getNomeFile();
		this.ordineInclusione = ordineInclusione;
		this.atto = atto;
		this.file = file;
	}

	public DocumentoInformatico(  File file, Parere parere, Integer ordineInclusione ) {
		super();
		this.nomeFile = file.getNomeFile();
		this.ordineInclusione = ordineInclusione;
		this.parere = parere;
		this.file = file;
	}
	
	public DocumentoInformatico(  File file, Verbale verbale, Integer ordineInclusione ) {
		super();
		this.nomeFile = file.getNomeFile();
		this.ordineInclusione = ordineInclusione;
		this.verbale = verbale;
		this.file = file;
//		this.titolo = String.format("Allegato %s", ordineInclusione);
	}

	public DocumentoInformatico() {
	}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "titolo")
    private String titolo;

    @Column(name = "nome_file")
    private String nomeFile;

    @Column(name = "ordine_inclusione")
    private Integer ordineInclusione;

    @Column(name = "pubblicabile")
    private Boolean pubblicabile;

    @Column(name = "parte_integrante")
    private Boolean parteIntegrante;
    
    @Column(name = "omissis")
    private Boolean omissis;
    
    @Column(name = "allegato_provvedimento")
    private Boolean allegatoProvvedimento;
    
    @Column(name = "oggetto")
    private String oggetto;

    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="atto_id",insertable=true, updatable=false)
    private Atto atto;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parere_id",insertable=true, updatable=false)
    private Parere parere;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="resoconto_id",insertable=true, updatable=false)
    private Resoconto resoconto;

    @ManyToOne
    private File file;

    @ManyToOne
    private File fileomissis;

    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="lettera_id",insertable=true, updatable=false)
    private Lettera lettera;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="verbale_id",insertable=true, updatable=false)
    private Verbale verbale;

    @ManyToOne
    private Avanzamento avanzamento;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_protocollo")
	private LocalDate dataProtocollo;
    
    @Column(name = "numero_protocollo")
    private String numeroProtocollo;
    
    @Column(name = "note")
    private String note;
    
    @ManyToOne
    @JoinColumn(name = "documento_rif_id", insertable=true, updatable=false)
    private DocumentoPdf documentoRiferimento;
    
    @ManyToOne
    @JoinColumn(name="id_tipo_allegato", insertable=true, updatable=true)
    private TipoAllegato tipoAllegato;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public Integer getOrdineInclusione() {
        return ordineInclusione;
    }

    public void setOrdineInclusione(Integer ordineInclusione) {
        this.ordineInclusione = ordineInclusione;
    }

    public Boolean getParteIntegrante() {
		return parteIntegrante;
	}

	public void setParteIntegrante(Boolean parteIntegrante) {
		this.parteIntegrante = parteIntegrante;
	}

	public Boolean getPubblicabile() {
        return pubblicabile;
    }

    public void setPubblicabile(Boolean pubblicabile) {
        this.pubblicabile = pubblicabile;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public Atto getAtto() {
        return atto;
    }

    public void setAtto(Atto atto) {
        this.atto = atto;
    }

    public Parere getParere() {
        return parere;
    }

    public void setParere(Parere parere) {
        this.parere = parere;
    }

    public Resoconto getResoconto() {
        return resoconto;
    }

    public void setResoconto(Resoconto resoconto) {
        this.resoconto = resoconto;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Lettera getLettera() {
        return lettera;
    }

    public void setLettera(Lettera lettera) {
        this.lettera = lettera;
    }

    public Verbale getVerbale() {
        return verbale;
    }

    public void setVerbale(Verbale verbale) {
        this.verbale = verbale;
    }

    public Avanzamento getAvanzamento() {
        return avanzamento;
    }

    public void setAvanzamento(Avanzamento avanzamento) {
        this.avanzamento = avanzamento;
    }

    public File getFileomissis() {
		return fileomissis;
	}

	public void setFileomissis(File fileomissis) {
		this.fileomissis = fileomissis;
	}

	public Boolean getOmissis() {
		return omissis;
	}

	public void setOmissis(Boolean omissis) {
		this.omissis = omissis;
	}

	public LocalDate getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(LocalDate dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public DocumentoPdf getDocumentoRiferimento() {
		return documentoRiferimento;
	}

	public void setDocumentoRiferimento(DocumentoPdf documentoRiferimento) {
		this.documentoRiferimento = documentoRiferimento;
	}

	public Boolean getAllegatoProvvedimento() {
		return allegatoProvvedimento;
	}

	public void setAllegatoProvvedimento(Boolean allegatoProvvedimento) {
		this.allegatoProvvedimento = allegatoProvvedimento;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String getCmisDocumentId() {
		if(this.getFile()!=null && this.getFile().getCmisObjectId()!=null && !this.getFile().getCmisObjectId().trim().isEmpty()) {
			return this.getFile().getCmisObjectId().trim();
		}else {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((allegatoProvvedimento == null) ? 0 : allegatoProvvedimento.hashCode());
		result = prime * result + ((atto == null) ? 0 : atto.hashCode());
		result = prime * result + ((avanzamento == null) ? 0 : avanzamento.hashCode());
		result = prime * result + ((dataProtocollo == null) ? 0 : dataProtocollo.hashCode());
		result = prime * result + ((documentoRiferimento == null) ? 0 : documentoRiferimento.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((fileomissis == null) ? 0 : fileomissis.hashCode());
		result = prime * result + ((lettera == null) ? 0 : lettera.hashCode());
		result = prime * result + ((nomeFile == null) ? 0 : nomeFile.hashCode());
		result = prime * result + ((numeroProtocollo == null) ? 0 : numeroProtocollo.hashCode());
		result = prime * result + ((oggetto == null) ? 0 : oggetto.hashCode());
		result = prime * result + ((omissis == null) ? 0 : omissis.hashCode());
		result = prime * result + ((ordineInclusione == null) ? 0 : ordineInclusione.hashCode());
		result = prime * result + ((parere == null) ? 0 : parere.hashCode());
		result = prime * result + ((parteIntegrante == null) ? 0 : parteIntegrante.hashCode());
		result = prime * result + ((pubblicabile == null) ? 0 : pubblicabile.hashCode());
		result = prime * result + ((resoconto == null) ? 0 : resoconto.hashCode());
		result = prime * result + ((titolo == null) ? 0 : titolo.hashCode());
		result = prime * result + ((verbale == null) ? 0 : verbale.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentoInformatico other = (DocumentoInformatico) obj;
		if (allegatoProvvedimento == null) {
			if (other.allegatoProvvedimento != null)
				return false;
		} else if (!allegatoProvvedimento.equals(other.allegatoProvvedimento))
			return false;
		if (atto == null) {
			if (other.atto != null)
				return false;
		} else if (!atto.equals(other.atto))
			return false;
		if (avanzamento == null) {
			if (other.avanzamento != null)
				return false;
		} else if (!avanzamento.equals(other.avanzamento))
			return false;
		if (dataProtocollo == null) {
			if (other.dataProtocollo != null)
				return false;
		} else if (!dataProtocollo.equals(other.dataProtocollo))
			return false;
		if (documentoRiferimento == null) {
			if (other.documentoRiferimento != null)
				return false;
		} else if (!documentoRiferimento.equals(other.documentoRiferimento))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (fileomissis == null) {
			if (other.fileomissis != null)
				return false;
		} else if (!fileomissis.equals(other.fileomissis))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lettera == null) {
			if (other.lettera != null)
				return false;
		} else if (!lettera.equals(other.lettera))
			return false;
		if (nomeFile == null) {
			if (other.nomeFile != null)
				return false;
		} else if (!nomeFile.equals(other.nomeFile))
			return false;
		if (numeroProtocollo == null) {
			if (other.numeroProtocollo != null)
				return false;
		} else if (!numeroProtocollo.equals(other.numeroProtocollo))
			return false;
		if (oggetto == null) {
			if (other.oggetto != null)
				return false;
		} else if (!oggetto.equals(other.oggetto))
			return false;
		if (omissis == null) {
			if (other.omissis != null)
				return false;
		} else if (!omissis.equals(other.omissis))
			return false;
		if (ordineInclusione == null) {
			if (other.ordineInclusione != null)
				return false;
		} else if (!ordineInclusione.equals(other.ordineInclusione))
			return false;
		if (parere == null) {
			if (other.parere != null)
				return false;
		} else if (!parere.equals(other.parere))
			return false;
		if (parteIntegrante == null) {
			if (other.parteIntegrante != null)
				return false;
		} else if (!parteIntegrante.equals(other.parteIntegrante))
			return false;
		if (pubblicabile == null) {
			if (other.pubblicabile != null)
				return false;
		} else if (!pubblicabile.equals(other.pubblicabile))
			return false;
		if (resoconto == null) {
			if (other.resoconto != null)
				return false;
		} else if (!resoconto.equals(other.resoconto))
			return false;
		if (titolo == null) {
			if (other.titolo != null)
				return false;
		} else if (!titolo.equals(other.titolo))
			return false;
		if (verbale == null) {
			if (other.verbale != null)
				return false;
		} else if (!verbale.equals(other.verbale))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return "DocumentoInformatico{" +
                "id=" + id +
                ", titolo='" + titolo + "'" +
                ", nomeFile='" + nomeFile + "'" +
                ", ordineInclusione='" + ordineInclusione + "'" +
                ", pubblicabile='" + pubblicabile + "'" +
                ", oggetto='" + oggetto + "'" +
                '}';
    }

	public TipoAllegato getTipoAllegato() {
		return tipoAllegato;
	}

	public void setTipoAllegato(TipoAllegato tipoAllegato) {
		this.tipoAllegato = tipoAllegato;
	}
}

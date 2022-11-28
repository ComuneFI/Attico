package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
 * A OrdineGiorno.
 */
@Entity
@Table(name = "ORDINEGIORNO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrdineGiorno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "progressivo_odg")
    private String progressivoOdgSeduta;

    @Column(name = "protocollo")
    private String protocollo;
    
    @Column(name = "oggetto")
    private String oggetto;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "data_pubblicazione_sito")
    private LocalDate dataPubblicazioneSito;

    @OneToMany(mappedBy = "ordineGiorno")
	@OrderBy(value = " ordine_odg ASC")
	private List<AttiOdg> attos = new ArrayList<AttiOdg>();

    @ManyToOne
    @JoinColumn(name="sedutagiunta_id", insertable = true, updatable = false)
    private SedutaGiunta sedutaGiunta;

    @ManyToOne
    private TipoOdg tipoOdg;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "preambolo_id", insertable = true, updatable = false)
	private SezioneTesto preambolo = new SezioneTesto();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="ordinegiorno_id", insertable=true, updatable=false)
  	@OrderBy(value = "created_date DESC")
  	private List<DocumentoPdf> documentiPdf = new ArrayList<DocumentoPdf>();
	
	@ManyToOne
	@JoinColumn(name="sottoscrittore_profilo_id")
    private Profilo sottoscrittore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DocumentoPdf> getDocumentiPdf() {
		return documentiPdf;
	}

	public void setDocumentiPdf(List<DocumentoPdf> documentiPdf) {
		this.documentiPdf = documentiPdf;
	}

	public String getProtocollo() {
        return protocollo;
    }

    public void setProtocollo(String protocollo) {
        this.protocollo = protocollo;
    }

    public LocalDate getDataPubblicazioneSito() {
        return dataPubblicazioneSito;
    }

    public void setDataPubblicazioneSito(LocalDate dataPubblicazioneSito) {
        this.dataPubblicazioneSito = dataPubblicazioneSito;
    }

    public SedutaGiunta getSedutaGiunta() {
        return sedutaGiunta;
    }

    public void setSedutaGiunta(SedutaGiunta sedutaGiunta) {
        this.sedutaGiunta = sedutaGiunta;
    }

    public TipoOdg getTipoOdg() {
        return tipoOdg;
    }

    public void setTipoOdg(TipoOdg tipoOdg) {
        this.tipoOdg = tipoOdg;
    }
    
    

    public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public List<AttiOdg> getAttos() {
		return attos;
	}

	public void setAttos(List<AttiOdg> attos) {
		this.attos = attos;
	}
	
	public String getProgressivoOdgSeduta() {
		return progressivoOdgSeduta;
	}

	public void setProgressivoOdgSeduta(String progressivoOdgSeduta) {
		this.progressivoOdgSeduta = progressivoOdgSeduta;
	}
	
	public Profilo getSottoscrittore() {
		return sottoscrittore;
	}

	public void setSottoscrittore(Profilo sottoscrittore) {
		this.sottoscrittore = sottoscrittore;
	}

	public SezioneTesto getPreambolo() {
		return preambolo;
	}

	public void setPreambolo(SezioneTesto preambolo) {
		this.preambolo = preambolo;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrdineGiorno ordineGiorno = (OrdineGiorno) o;

        if ( ! Objects.equals(id, ordineGiorno.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OrdineGiorno{" +
                "id=" + id +
               
                ", protocollo='" + protocollo + "'" +
                ", dataPubblicazioneSito='" + dataPubblicazioneSito + "'" +
                '}';
    }
}

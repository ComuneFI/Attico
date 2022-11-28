package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Verbale.
 */
@Entity
@Table(name = "VERBALE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Verbale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "stato")
    private String stato;
    
    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "verbale_testo_id",insertable=true, updatable=false)
	private SezioneTesto narrativa = new SezioneTesto();
    
    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "verbale_notefinali_id",insertable=true, updatable=false)
	private SezioneTesto noteFinali = new SezioneTesto();

    @Column(name = "organo_deliberante")
    private String organoDeliberante;

    @ManyToOne
    @JoinColumn(name = "sedutagiunta_id", insertable=true, updatable=false)
    private SedutaGiunta sedutaGiunta;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
    @JoinColumn(name="verbale_id", insertable=true, updatable=false)
  	@OrderBy(value = "created_date DESC")
  	private List<DocumentoPdf> documentiPdf = new ArrayList<DocumentoPdf>();
    
    @OneToMany(mappedBy = "verbale", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
	@OrderBy(value = " ordine_inclusione ASC")
	private Set<DocumentoInformatico> allegati = new HashSet<DocumentoInformatico>();
    
    @OneToMany(mappedBy = "verbale", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval=true)
	@OrderBy(value = " ordine_firma ASC")
	private Set<SottoscrittoreSedutaGiunta> sottoscrittori = new TreeSet<SottoscrittoreSedutaGiunta>();

    public Verbale() {
//    	this.narrativa = new SezioneTesto();
//    	this.documentiPdf = new ArrayList<DocumentoPdf>();
//    	this.allegati = new HashSet<DocumentoInformatico>();
//    	this.sottoscrittori = new TreeSet<SottoscrittoreSedutaGiunta>();
    }
    
    public Verbale(Long _id) {
//    	super();
		this.id = _id;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public Set<DocumentoInformatico> getAllegati() {
		return allegati;
	}

	public void setAllegati(Set<DocumentoInformatico> allegati) {
		this.allegati = allegati;
	}

	public List<DocumentoPdf> getDocumentiPdf() {
		return documentiPdf;
	}

	public void setDocumentiPdf(List<DocumentoPdf> documentiPdf) {
		this.documentiPdf = documentiPdf;
	}

	public String getOrganoDeliberante() {
        return organoDeliberante;
    }

    public void setOrganoDeliberante(String organoDeliberante) {
        this.organoDeliberante = organoDeliberante;
    }

    public SedutaGiunta getSedutaGiunta() {
        return sedutaGiunta;
    }

    public void setSedutaGiunta(SedutaGiunta sedutaGiunta) {
        this.sedutaGiunta = sedutaGiunta;
    }
    
    public SezioneTesto getNarrativa() {
		return narrativa;
	}

	public void setNarrativa(SezioneTesto narrativa) {
		this.narrativa = narrativa;
	}

	public SezioneTesto getNoteFinali() {
		return noteFinali;
	}

	public void setNoteFinali(SezioneTesto noteFinali) {
		this.noteFinali = noteFinali;
	}

	public Set<SottoscrittoreSedutaGiunta> getSottoscrittori() {
		return sottoscrittori;
	}

	public void setSottoscrittori(Set<SottoscrittoreSedutaGiunta> sottoscrittori) {
		this.sottoscrittori = sottoscrittori;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Verbale verbale = (Verbale) o;

        if ( ! Objects.equals(id, verbale.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
    	String retValue = "Verbale{" +
                "id=" + id +
                ", organoDeliberante='" + organoDeliberante + "'"
                ;
    	retValue += '}'; 
        
    	return retValue;
    }
}

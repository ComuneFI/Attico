package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

/**
 * A SottoscrittoreAtto.
 */
@Entity
@Table(name = "SOTTOSCRITTOREATTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SottoscrittoreAtto implements Serializable, Comparable<SottoscrittoreAtto> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "enabled")
	private Boolean enabled;

    @Column(name = "editor")
    private Boolean editor;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_firma")
    private DateTime dataFirma;

    @Column(name = "ordine_firma")
    private Integer ordineFirma;
    
    @ManyToOne
    private Aoo aoo;
    
    @Transient
	@JsonProperty
	private Aoo aooNonProponente;
    
    @ManyToOne
    private Atto atto;
   
    @ManyToOne
    @JoinColumn(name="documento_id", insertable=true, updatable=false)
    private DocumentoPdf documentoPdf;
    
    @ManyToOne
    private Profilo profilo;

    @Column(name = "descrizione_qualifica")
    private String descrizioneQualifica;
    
    @ManyToOne
    private QualificaProfessionale qualificaProfessionale;

    @Column(name = "tipologia")
    private Integer tipologia;
    
    public SottoscrittoreAtto() {
		this.enabled = true;
	}
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public Boolean getEditor() {
        return editor;
    }

    public void setEditor(Boolean editor) {
        this.editor = editor;
    }

    public Integer getOrdineFirma() {
		return ordineFirma;
	}

	public void setOrdineFirma(Integer ordineFirma) {
		this.ordineFirma = ordineFirma;
	}
	
	public DateTime getDataFirma() {
		return dataFirma;
	}

	public void setDataFirma(DateTime dataFirma) {
		this.dataFirma = dataFirma;
	}

	public Atto getAtto() {
        return atto;
    }

    public void setAtto(Atto atto) {
        this.atto = atto;
    }

    public DocumentoPdf getDocumentoPdf() {
		return documentoPdf;
	}

	public void setDocumentoPdf(DocumentoPdf documentoPdf) {
		this.documentoPdf = documentoPdf;
	}

	public Profilo getProfilo() {
        return profilo;
    }

    public void setProfilo(Profilo profilo) {
        this.profilo = profilo;
    }

    public String getDescrizioneQualifica() {
		return descrizioneQualifica;
	}

	public void setDescrizioneQualifica(String descrizioneQualifica) {
		this.descrizioneQualifica = descrizioneQualifica;
	}

	public QualificaProfessionale getQualificaProfessionale() {
		return qualificaProfessionale;
	}

	public void setQualificaProfessionale(
			QualificaProfessionale qualificaProfessionale) {
		this.qualificaProfessionale = qualificaProfessionale;
	}

	public Integer getTipologia() {
		return tipologia;
	}

	public void setTipologia(Integer tipologia) {
		this.tipologia = tipologia;
	}


    public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Aoo getAooNonProponente() {
		return aooNonProponente;
	}

	public void setAooNonProponente(Aoo aooNonProponente) {
		this.aooNonProponente = aooNonProponente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atto == null) ? 0 : atto.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((profilo == null) ? 0 : profilo.hashCode());
		result = prime
				* result
				+ ((qualificaProfessionale == null) ? 0
						: qualificaProfessionale.hashCode());
		return result;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SottoscrittoreAtto sottoscrittoreAtto = (SottoscrittoreAtto) o;

        if ( ! Objects.equals(id, sottoscrittoreAtto.id)) return false;

        return true;
    }
    
    

	@Override
    public String toString() {
        return "SottoscrittoreAtto{" +
                "id=" + id +
                ", editor='" + editor + "'" +
                '}';
    }

	@Override
	public int compareTo(SottoscrittoreAtto b) {
		if (this.getOrdineFirma() == null || b.getOrdineFirma() == null) {
    		return 1;
    	}

        return this.getOrdineFirma().compareTo(b.getOrdineFirma());
	}
}

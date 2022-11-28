package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A SottoscrittoreAtto.
 */
@Entity
@Table(name = "SOTTOSCRITTORESEDUTAGIUNTA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SottoscrittoreSedutaGiunta implements Serializable, Comparable<SottoscrittoreSedutaGiunta> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2858645296899625950L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "editor")
    private Boolean editor;
    
    @Column(name = "firmato")
    private Boolean firmato;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_firma")
    private DateTime dataFirma;

    @Column(name = "ordine_firma")
    private Integer ordineFirma;
    
    @ManyToOne
    private Verbale verbale;
    
    @ManyToOne
    private SedutaGiunta sedutaresoconto;
    
    @ManyToOne
    private SedutaGiunta sedutaGiunta;
    
    @ManyToOne
    private OrdineGiorno odg;

    @ManyToOne
    private Profilo profilo;

    @ManyToOne
    private QualificaProfessionale qualificaProfessionale;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEditor() {
        return editor;
    }

    public void setEditor(Boolean editor) {
        this.editor = editor;
    }

    public Boolean getFirmato() {
		return firmato;
	}

	public void setFirmato(Boolean firmato) {
		this.firmato = firmato;
	}

	public DateTime getDataFirma() {
		return dataFirma;
	}

	public void setDataFirma(DateTime dataFirma) {
		this.dataFirma = dataFirma;
	}

	public Integer getOrdineFirma() {
		return ordineFirma;
	}

	public void setOrdineFirma(Integer ordineFirma) {
		this.ordineFirma = ordineFirma;
	}
 
	public Verbale getVerbale() {
		return verbale;
	}

	public void setVerbale(Verbale verbale) {
		this.verbale = verbale;
	}

	public SedutaGiunta getSedutaGiunta() {
		return sedutaGiunta;
	}

	public void setSedutaGiunta(SedutaGiunta sedutaGiunta) {
		this.sedutaGiunta = sedutaGiunta;
	}

	public OrdineGiorno getOdg() {
		return odg;
	}

	public void setOdg(OrdineGiorno odg) {
		this.odg = odg;
	}

	public Profilo getProfilo() {
        return profilo;
    }

    public void setProfilo(Profilo profilo) {
        this.profilo = profilo;
    }

    public QualificaProfessionale getQualificaProfessionale() {
		return qualificaProfessionale;
	}

	public void setQualificaProfessionale(
			QualificaProfessionale qualificaProfessionale) {
		this.qualificaProfessionale = qualificaProfessionale;
	}

    public SedutaGiunta getSedutaresoconto() {
		return sedutaresoconto;
	}

	public void setSedutaresoconto(SedutaGiunta sedutaresoconto) {
		this.sedutaresoconto = sedutaresoconto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((sedutaGiunta == null) ? 0 : sedutaGiunta.hashCode());
		result = prime * result + ((odg == null) ? 0 : odg.hashCode());
		result = prime * result + ((verbale == null) ? 0 : verbale.hashCode());
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

        SottoscrittoreSedutaGiunta sottoscrittoreAtto = (SottoscrittoreSedutaGiunta) o;

        if ( ! Objects.equals(id, sottoscrittoreAtto.id)) return false;

        return true;
    }

	@Override
    public String toString() {
        return "SottoscrittoreSedutaGiunta{" +
                "id=" + id +
                ", editor='" + editor + "'" +
                ", profilo=[ '" + profilo.getUtente().getCognome() + " - " + profilo.getUtente().getNome() + " ]'" +
                '}';
    }

	@Override
	public int compareTo(SottoscrittoreSedutaGiunta b) {
		if (this.getOrdineFirma() == null || b.getOrdineFirma() == null) {
    		return 1;
    	}

        return this.getOrdineFirma().compareTo(b.getOrdineFirma());
	}
}

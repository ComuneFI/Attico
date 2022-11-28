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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SottoscrittoreAtto.
 */
@Entity
@Table(name = "SOTTOSCRITTOREPARERE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SottoscrittoreParere implements Serializable, Comparable<SottoscrittoreParere> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2858645296899625950L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "editor")
    private Boolean editor;

    @Column(name = "ordine_firma")
    private Integer ordineFirma;
    
    @ManyToOne
    @JoinColumn(name="parere_id",insertable=true, updatable=false)
    private Parere parere;

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

    public Integer getOrdineFirma() {
		return ordineFirma;
	}

	public void setOrdineFirma(Integer ordineFirma) {
		this.ordineFirma = ordineFirma;
	}
 

    public Parere getParere() {
		return parere;
	}

	public void setParere(Parere parere) {
		this.parere = parere;
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

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((parere == null) ? 0 : parere.hashCode());
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

        SottoscrittoreParere sottoscrittoreAtto = (SottoscrittoreParere) o;

        if ( ! Objects.equals(id, sottoscrittoreAtto.id)) return false;

        return true;
    }

	@Override
    public String toString() {
        return "SottoscrittoreParere{" +
                "id=" + id +
                ", editor='" + editor + "'" +
                '}';
    }

	@Override
	public int compareTo(SottoscrittoreParere b) {
		if (this.getOrdineFirma() == null || b.getOrdineFirma() == null) {
    		return 1;
    	}

        return this.getOrdineFirma().compareTo(b.getOrdineFirma());
	}
}

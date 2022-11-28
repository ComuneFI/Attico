package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A QualificaProfessionale.
 */
@Entity
@Table(name = "QUALIFICAPROFESSIONALE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class QualificaProfessionale implements Serializable {

    public QualificaProfessionale(Long id) {
		super();
		this.id = id;
	}
    
    public QualificaProfessionale() {
		super();
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "denominazione")
    private String denominazione;

	@Column(name = "enabled")
	private Boolean enabled;
	
	@Transient
	@JsonProperty
	private Boolean atti;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

	public Boolean getAtti() {
		return atti;
	}

	public void setAtti(Boolean atti) {
		this.atti = atti;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QualificaProfessionale qualificaProfessionale = (QualificaProfessionale) o;

        if ( ! Objects.equals(id, qualificaProfessionale.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QualificaProfessionale{" +
                "id=" + id +
                ", denominazione='" + denominazione + "'" +
                '}';
    }
}

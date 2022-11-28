package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * A Dato.
 */
@Entity
@Table(name = "DATO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Dato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "etichetta")
    private String etichetta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato")
    private TipoDatoEnum tipoDato;
    
    @Column(name = "multivalore",length=21844)
    private String multivalore;

    
    @Transient
    @JsonSerialize
    private Set<String> options = new HashSet<String>();
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEtichetta() {
        return etichetta;
    }

    public void setEtichetta(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getMultivalore() {
        return multivalore;
    }

    public void setMultivalore(String multivalore) {
        this.multivalore = multivalore;
    }


   
    
    public TipoDatoEnum getTipoDato() {
		return tipoDato;
	}

	public void setTipoDato(TipoDatoEnum tipoDato) {
		this.tipoDato = tipoDato;
	}

	public Set<String> getOptions() {
		return options;
	}

	public void setOptions(Set<String> options) {
		this.options = options;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dato dato = (Dato) o;

        if ( ! Objects.equals(id, dato.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Dato{" +
                "id=" + id +
                ", etichetta='" + etichetta + "'" +
                ", multivalore='" + multivalore + "'" +
                '}';
    }
}

package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A ModelloCampo.
 */
@Entity
@Table(name = "SEZIONETESTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SezioneTesto extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Type(type="org.hibernate.type.MaterializedClobType")
    @Column(name = "testo")
    private String testo;
    
    @Column(name = "stampa")
    private Boolean stampa;
    
    public SezioneTesto(String testo){
    	this.testo = testo;
    }
    
    public SezioneTesto(){
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
	
	public Boolean getStampa() {
		return stampa;
	}

	public void setStampa(Boolean stampa) {
		this.stampa = stampa;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SezioneTesto modelloCampo = (SezioneTesto) o;

        if ( ! Objects.equals(id, modelloCampo.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ModelloCampo{" +
                "id=" + id +
                '}';
    }
}

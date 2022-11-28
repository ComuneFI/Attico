package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StoricoAttoGiunta.
 */
@Entity
@Table(name = "STORICO_PRESENTE_GIUNTA" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StoricoPresenteGiunta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StoricoPresenteGiunta(final Long id) {
		this.id = id;
	}

	public StoricoPresenteGiunta() {
	}
	
	@Id
	@Column(name = "idpresente_giunta")
	private Long id;

	@ManyToOne
	@JoinColumn(name="atto_giunta")
	private StoricoAttoGiunta attoGiunta;

	@Column(name = "descrizione", length = 500, insertable = true, updatable = false)
	private String descrizione;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StoricoAttoGiunta getAttoGiunta() {
		return attoGiunta;
	}

	public void setAttoGiunta(StoricoAttoGiunta attoGiunta) {
		this.attoGiunta = attoGiunta;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        StoricoPresenteGiunta sag = (StoricoPresenteGiunta) o;
        
        if ( ! Objects.equals(id, sag.id)) return false;

        return true;
	}
	
	@Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
	

}

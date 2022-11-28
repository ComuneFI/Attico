package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A TipoDeterminazione.
 */
@Entity
@Table(name = "TIPODETERMINAZIONE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoDeterminazione implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4967853032712147568L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "descrizione")
    private String descrizione;
    
    @Column(name = "codice")
    private String codice;
    
    @Transient
	@JsonProperty
	private Boolean atti;
    
    @Column(name = "stato_trasparenza")
    private String statoTrasparenza;
    
    @Column(name = "file_visibili_in_trasparenza")
    private Boolean fileVisibiliInTrasparenza;
    
    @Column(name = "enabled", columnDefinition = "tinyint(4) default 1")
	private Boolean enabled;
    
    @ManyToMany(fetch = FetchType.EAGER)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinTable(name = "tipodeterminazione_tipoatto", joinColumns = { 
			@JoinColumn(name = "tipodeterminazione_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "tipoatto_id", 
					nullable = false, updatable = false) })
	private Set<TipoAtto> tipiAtto = new HashSet<TipoAtto>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Boolean getAtti() {
		return atti;
	}

	public void setAtti(Boolean atti) {
		this.atti = atti;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getStatoTrasparenza() {
		return statoTrasparenza;
	}

	public void setStatoTrasparenza(String statoTrasparenza) {
		this.statoTrasparenza = statoTrasparenza;
	}
	
	public Boolean getFileVisibiliInTrasparenza() {
		return fileVisibiliInTrasparenza;
	}

	public void setFileVisibiliInTrasparenza(Boolean fileVisibiliInTrasparenza) {
		this.fileVisibiliInTrasparenza = fileVisibiliInTrasparenza;
	}
	
	public Set<TipoAtto> getTipiAtto() {
		return tipiAtto;
	}

	public void setTipiAtto(Set<TipoAtto> tipiAtto) {
		this.tipiAtto = tipiAtto;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TipoDeterminazione tipoDeterminazione = (TipoDeterminazione) o;

        if ( ! Objects.equals(id, tipoDeterminazione.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TipoDeterminazione{" +
                "id=" + id +
                ", descrizione='" + descrizione + "'" +
                '}';
    }
}

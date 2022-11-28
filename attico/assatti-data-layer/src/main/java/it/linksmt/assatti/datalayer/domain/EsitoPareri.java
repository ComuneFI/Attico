package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A EsitoPareri.
 */
@Entity
@Table(name = "ESITO_PARERI")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EsitoPareri implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7813196757358834394L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codice")
    private String codice;

    @Column(name = "valore")
    private String valore;
    
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tipoatto_id",insertable=true, updatable = true)
	private TipoAtto tipoAtto;
    
    @Column(name = "tipo")
    private String tipo;

        
    @Column(name = "enabled")
    private Boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }
    
	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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

        EsitoPareri ruolo = (EsitoPareri) o;

        if ( ! Objects.equals(id, ruolo.id)) {
			return false;
		}

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ruolo{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", valore='" + valore + "'" +
                ", tipoAtto='" + tipoAtto.getCodice()+ "'" +
                '}';
    }
}

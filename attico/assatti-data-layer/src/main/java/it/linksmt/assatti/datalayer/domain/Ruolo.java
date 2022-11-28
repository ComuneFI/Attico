package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ruolo.
 */
@Entity
@Table(name = "RUOLO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ruolo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7813196757358834394L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codice")
    private String codice;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "haqualifica")
    private Boolean haqualifica;
    
    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "only_admin")
	private Boolean onlyAdmin;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "tipo")
    private TipoRuoloEnum tipo;

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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

	public Boolean getHaqualifica() {
        return haqualifica;
    }

    public void setHaqualifica(Boolean haqualifica) {
        this.haqualifica = haqualifica;
    }

    public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public TipoRuoloEnum getTipo() {
		return tipo;
	}

	public void setTipo(TipoRuoloEnum tipo) {
		this.tipo = tipo;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Ruolo ruolo = (Ruolo) o;

        if ( ! Objects.equals(id, ruolo.id)) {
			return false;
		}

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Boolean getOnlyAdmin() {
		return onlyAdmin;
	}

	public void setOnlyAdmin(Boolean onlyAdmin) {
		this.onlyAdmin = onlyAdmin;
	}

	@Override
    public String toString() {
        return "Ruolo{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", descrizione='" + descrizione + "'" +
                ", haqualifica='" + haqualifica + "'" +
                ", onlyAdmin='" + onlyAdmin + "'" +
                '}';
    }
}

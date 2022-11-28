package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TipoDocumentoSerie.
 */
@Entity
@Table(name = "TIPODOCUMENTOSERIE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoDocumentoSerie implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "descrizione")
    private String descrizione;
    
    @Column(name = "codice", unique=true)
    private String codice;
    
    @Column(name = "enable")
    private Boolean enable;
    
//    @Transient
//    @JsonProperty
//    private TipoAtto tipoAtto;
//    
//    
//	@Column(name = "tipoatto_id")
//	private Long idTipoAtto;
//    
//    @Column(name = "is_proposta")
//    private Boolean isProposta;

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

    public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

//	public TipoAtto getTipoAtto() {
//		return tipoAtto;
//	}
//
//	public void setTipoAtto(TipoAtto tipoAtto) {
//		this.tipoAtto = tipoAtto;
//	}
//
//	public Long getIdTipoAtto() {
//		return idTipoAtto;
//	}
//
//	public void setIdTipoAtto(Long idTipoAtto) {
//		this.idTipoAtto = idTipoAtto;
//	}
//
//	public Boolean getIsProposta() {
//		return isProposta;
//	}
//
//	public void setIsProposta(Boolean isProposta) {
//		this.isProposta = isProposta;
//	}

	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TipoDocumentoSerie tipoDocumento = (TipoDocumentoSerie) o;

        if ( ! Objects.equals(id, tipoDocumento.id)) return false;

        return true;
    }

	@Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TipoDocumento{" +
                "id=" + id +
                ", descrizione='" + descrizione + "'" +
                '}';
    }
}

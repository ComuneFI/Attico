package it.linksmt.assatti.datalayer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TipoDocumento.
 */
@Entity
@Table(name = "TIPODOCUMENTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoDocumento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "descrizione")
    private String descrizione;
    
    @Column(name = "codice", unique=true)
    private String codice;
    
    @Column(name="riversamento_tipoatto")
    private Boolean riversamentoTipoatto;
    
    @Column(name = "dms_content_type")
	private String dmsContentType;
    
    @Column(name = "specifica_filename")
    private String specificaFilename;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DocumentoTypeEnum type;

    public DocumentoTypeEnum getType() {
		return type;
	}

	public void setType(DocumentoTypeEnum type) {
		this.type = type;
	}

	public String getSpecificaFilename() {
		return specificaFilename;
	}

	public void setSpecificaFilename(String specificaFilename) {
		this.specificaFilename = specificaFilename;
	}

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

	public Boolean getRiversamentoTipoatto() {
		return riversamentoTipoatto;
	}

	public void setRiversamentoTipoatto(Boolean riversamentoTipoatto) {
		this.riversamentoTipoatto = riversamentoTipoatto;
	}

	public String getDmsContentType() {
		return dmsContentType;
	}

	public void setDmsContentType(String dmsContentType) {
		this.dmsContentType = dmsContentType;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TipoDocumento tipoDocumento = (TipoDocumento) o;

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

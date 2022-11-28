package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Classificazione.
 */
@Entity
@Table(name = "CLASSIFICAZIONE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Classificazione implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name="titolario")
	private String titolario;
	
	@Column(name="vocetitolariodescrizione")
	private String voceTitolarioDescrizione;
	
	@Column(name="vocetitolariocodice")
	private String voceTitolarioCodice;

    public String getVoceTitolarioDescrizione() {
		return voceTitolarioDescrizione;
	}

	public String getVoceTitolarioCodice() {
		return voceTitolarioCodice;
	}

	public void setVoceTitolarioDescrizione(String voceTitolarioDescrizione) {
		this.voceTitolarioDescrizione = voceTitolarioDescrizione;
	}

	public void setVoceTitolarioCodice(String voceTitolarioCodice) {
		this.voceTitolarioCodice = voceTitolarioCodice;
	}

	@Column(name = "idtitolario")
    private String idTitolario;

    @Column(name = "idvocetitolario")
    private String idVoceTitolario;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "validodal")
    private LocalDate validoDal;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "validoal")
    private LocalDate validoAl;

    @ManyToOne
    private Aoo aoo;

    @ManyToOne
    private TipoDocumentoSerie tipoDocumentoSerie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdTitolario() {
        return idTitolario;
    }

    public void setIdTitolario(String idTitolario) {
        this.idTitolario = idTitolario;
    }

    public String getIdVoceTitolario() {
        return idVoceTitolario;
    }

    public void setIdVoceTitolario(String idVoceTitolario) {
        this.idVoceTitolario = idVoceTitolario;
    }

    public LocalDate getValidoDal() {
        return validoDal;
    }

    public void setValidoDal(LocalDate validoDal) {
        this.validoDal = validoDal;
    }

    public LocalDate getValidoAl() {
        return validoAl;
    }

    public void setValidoAl(LocalDate validoAl) {
        this.validoAl = validoAl;
    }

    public Aoo getAoo() {
        return aoo;
    }

    public void setAoo(Aoo aoo) {
        this.aoo = aoo;
    }

    public TipoDocumentoSerie getTipoDocumentoSerie() {
        return tipoDocumentoSerie;
    }

    public void setTipoDocumentoSerie(TipoDocumentoSerie tipoDocumentoSerie) {
        this.tipoDocumentoSerie = tipoDocumentoSerie;
    }

    public String getTitolario() {
		return titolario;
	}

	public void setTitolario(String titolario) {
		this.titolario = titolario;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Classificazione classificazione = (Classificazione) o;

        if ( ! Objects.equals(id, classificazione.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Classificazione{" +
                "id=" + id +
                ", idTitolario='" + idTitolario + "'" +
                ", idVoceTitolario='" + idVoceTitolario + "'" +
                ", validoDal='" + validoDal + "'" +
                ", validoAl='" + validoAl + "'" +
                '}';
    }
}

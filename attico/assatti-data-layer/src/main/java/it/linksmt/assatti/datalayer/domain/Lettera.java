package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Lettera.
 */
@Entity
@Table(name = "LETTERA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Lettera implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "tipo_lettera")
    private String tipoLettera;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "data_pubblicazione_sito")
    private LocalDate dataPubblicazioneSito;

    @Column(name = "testo")
    private String testo;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "data")
    private LocalDate data;

    @OneToMany
    @JoinColumn(name="lettera_id", insertable=true, updatable=false)
   	@OrderBy(value = "created_date DESC")
   	private List<DocumentoPdf> documentiPdf = new ArrayList<DocumentoPdf>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DocumentoPdf> getDocumentiPdf() {
		return documentiPdf;
	}

	public void setDocumentiPdf(List<DocumentoPdf> documentiPdf) {
		this.documentiPdf = documentiPdf;
	}

	public String getTipoLettera() {
        return tipoLettera;
    }

    public void setTipoLettera(String tipoLettera) {
        this.tipoLettera = tipoLettera;
    }

    public LocalDate getDataPubblicazioneSito() {
        return dataPubblicazioneSito;
    }

    public void setDataPubblicazioneSito(LocalDate dataPubblicazioneSito) {
        this.dataPubblicazioneSito = dataPubblicazioneSito;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lettera lettera = (Lettera) o;

        if ( ! Objects.equals(id, lettera.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Lettera{" +
                "id=" + id +
                ", tipoLettera='" + tipoLettera + "'" +
                ", dataPubblicazioneSito='" + dataPubblicazioneSito + "'" +
                ", testo='" + testo + "'" +
                ", data='" + data + "'" +
                '}';
    }
}

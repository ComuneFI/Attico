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
import javax.persistence.ManyToOne;
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
 * A Resoconto.
 */
@Entity
@Table(name = "RESOCONTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Resoconto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "organo_deliberante")
    private String organoDeliberante;
    
    @Column(name = "stato")
    private String stato;
    
	@ManyToOne
	@JoinColumn(name="sottoscrittore_profilo_id")
    private Profilo sottoscrittore;

    @Column(name = "tipo")
    private Integer tipo;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "data_pubblicazione_sito")
    private LocalDate dataPubblicazioneSito;

    @ManyToOne
    @JoinColumn(name="sedutagiunta_id")
    private SedutaGiunta sedutaGiunta;
    
    @OneToMany
   	@OrderBy(value = "created_date DESC")
    @JoinColumn(name="resoconto_id", insertable=true, updatable=false)
   	private List<DocumentoPdf> documentiPdf = new ArrayList<DocumentoPdf>();

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganoDeliberante() {
        return organoDeliberante;
    }

    public void setOrganoDeliberante(String organoDeliberante) {
        this.organoDeliberante = organoDeliberante;
    }

    public List<DocumentoPdf> getDocumentiPdf() {
		return documentiPdf;
	}

	public void setDocumentiPdf(List<DocumentoPdf> documentiPdf) {
		this.documentiPdf = documentiPdf;
	}

	public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataPubblicazioneSito() {
        return dataPubblicazioneSito;
    }

    public void setDataPubblicazioneSito(LocalDate dataPubblicazioneSito) {
        this.dataPubblicazioneSito = dataPubblicazioneSito;
    }

    public SedutaGiunta getSedutaGiunta() {
        return sedutaGiunta;
    }

    public void setSedutaGiunta(SedutaGiunta sedutaGiunta) {
        this.sedutaGiunta = sedutaGiunta;
    }
    

    public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public Profilo getSottoscrittore() {
		return sottoscrittore;
	}

	public void setSottoscrittore(Profilo sottoscrittore) {
		this.sottoscrittore = sottoscrittore;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resoconto resoconto = (Resoconto) o;

        if ( ! Objects.equals(id, resoconto.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Resoconto{" +
                "id=" + id +
                ", organoDeliberante='" + organoDeliberante + "'" +
                ", tipo='" + tipo + "'" +
                ", dataPubblicazioneSito='" + dataPubblicazioneSito + "'" +
                '}';
    }
}

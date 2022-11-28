package it.linksmt.assatti.datalayer.domain;

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

/**
 * A SchedaDato.
 */
@Entity
@Table(name = "SCHEDADATO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SchedaDato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "obbligatorio")
    private Boolean obbligatorio;

    @Column(name = "ordine")
    private Integer ordine;

    @ManyToOne
    private Scheda scheda;

    @ManyToOne(optional = false)
    private Dato dato;
    
    @Column(name = "style_css")
    private String styleCss;
    
    @Column(name = "hide_expression")
    private String hideExpression;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getObbligatorio() {
        return obbligatorio;
    }

    public void setObbligatorio(Boolean obbligatorio) {
        this.obbligatorio = obbligatorio;
    }

    public Integer getOrdine() {
        return ordine;
    }

    public void setOrdine(Integer ordine) {
        this.ordine = ordine;
    }

    public Scheda getScheda() {
        return scheda;
    }

    public void setScheda(Scheda scheda) {
        this.scheda = scheda;
    }

    public Dato getDato() {
        return dato;
    }

    public void setDato(Dato dato) {
        this.dato = dato;
    }

    public String getStyleCss() {
		return styleCss;
	}

	public void setStyleCss(String styleCss) {
		this.styleCss = styleCss;
	}

	public String getHideExpression() {
		return hideExpression;
	}

	public void setHideExpression(String hideExpression) {
		this.hideExpression = hideExpression;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SchedaDato schedaDato = (SchedaDato) o;

        if ( ! Objects.equals(id, schedaDato.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SchedaDato{" +
                "id=" + id +
                ", obbligatorio='" + obbligatorio + "'" +
                ", ordine='" + ordine + "'" +
                '}';
    }
}

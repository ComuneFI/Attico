package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A ModelloHtml.
 */
@Entity
@Table(name = "MODELLOHTML")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ModelloHtml implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "titolo")
    private String titolo;

    @Lob
    @Type(type="org.hibernate.type.MaterializedClobType")
    @Column(name = "html")
    private String html;
    
    @Column(name="page_orientation")
   	private Boolean pageOrientation;

	@ManyToOne
    @JoinColumn(name="tipodocumento_id")
    private TipoDocumento tipoDocumento;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Boolean getPageOrientation() {
		return pageOrientation;
	}

	public void setPageOrientation(Boolean pageOrientation) {
		this.pageOrientation = pageOrientation;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModelloHtml modelloHtml = (ModelloHtml) o;

        if ( ! Objects.equals(id, modelloHtml.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ModelloHtml{" +
                "id=" + id +
                ", titolo='" + titolo + "'" +
                ", html='" + html + "'" +
                '}';
    }
}

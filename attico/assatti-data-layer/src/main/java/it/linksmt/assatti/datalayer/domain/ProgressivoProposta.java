package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProgressivoProposta.
 */
@Entity
@Table(name = "PROGRESSIVOPROPOSTA",uniqueConstraints={@UniqueConstraint(columnNames={ "anno","aoo_id","tipoprogressivo_id"} )}/*TODO,
indexes={
		@Index(columnList="anno"),
		@Index(columnList="aoo_id")
		}*/
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProgressivoProposta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "anno")
    private Integer anno;

    @Column(name = "progressivo")
    private Integer progressivo;
    
    @ManyToOne
    @JoinColumn(name="tipoprogressivo_id",insertable=true,updatable=false)
    private TipoProgressivo tipoProgressivo;

    @ManyToOne
    @JoinColumn(name="aoo_id",insertable=true,updatable=false)
    private Aoo aoo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getProgressivo() {
        return progressivo;
    }

    public void setProgressivo(Integer progressivo) {
        this.progressivo = progressivo;
    }
    
    public TipoProgressivo getTipoProgressivo() {
		return tipoProgressivo;
	}

	public void setTipoProgressivo(TipoProgressivo tipoProgressivo) {
		this.tipoProgressivo = tipoProgressivo;
	}

	public Aoo getAoo() {
        return aoo;
    }

    public void setAoo(Aoo aoo) {
        this.aoo = aoo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProgressivoProposta progressivoProposta = (ProgressivoProposta) o;

        if ( ! Objects.equals(id, progressivoProposta.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProgressivoProposta{" +
                "id=" + id +
                ", anno='" + anno + "'" +
                ", progressivo='" + progressivo + "'" +
                '}';
    }
}

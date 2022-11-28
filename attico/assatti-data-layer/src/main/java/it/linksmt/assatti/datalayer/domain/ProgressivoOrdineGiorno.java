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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProgressivoProposta.
 */
@Entity
@Table(name = "PROGRESSIVOORDINEGIORNO",uniqueConstraints={@UniqueConstraint(columnNames={ "anno","tipoodg_id"} )}/*TODO,
indexes={
		@Index(columnList="anno"),
		@Index(columnList="tipoodg_id")
		}*/
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProgressivoOrdineGiorno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "anno")
    private Integer anno;

    @Column(name = "progressivo")
    private Integer progressivo;

    @ManyToOne
    private TipoOdg tipoOdg;

    

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

    public TipoOdg getTipoOdg() {
		return tipoOdg;
	}

	public void setTipoOdg(TipoOdg tipoOdg) {
		this.tipoOdg = tipoOdg;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProgressivoOrdineGiorno progressivoProposta = (ProgressivoOrdineGiorno) o;

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

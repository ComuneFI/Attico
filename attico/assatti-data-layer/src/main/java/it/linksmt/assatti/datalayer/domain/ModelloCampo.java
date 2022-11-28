package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * A ModelloCampo.
 */
@Entity
@Table(name = "MODELLOCAMPO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ModelloCampo extends AbstractAuditingEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
	@JoinColumn(name="tipo_atto_id", insertable = true, updatable = true)
	private TipoAtto tipoAtto;
    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aoo_id", insertable = true, updatable = true)
	private Aoo aoo;

    @Column(name = "codice")
    private String codice;
    
    @Column(name = "propagazione_aoo")
    private Boolean propagazioneAoo;

    @Column(name = "titolo")
    private String titolo;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Type(type="org.hibernate.type.MaterializedClobType")
    @Column(name = "testo")
    private String testo;

    @Column(name = "tipo_campo")
    private String tipoCampo;

    @ManyToOne
    private Profilo profilo;

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

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(String tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public Profilo getProfilo() {
        return profilo;
    }

    public void setProfilo(Profilo profilo) {
        this.profilo = profilo;
    }

    public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public Boolean getPropagazioneAoo() {
		return propagazioneAoo;
	}

	public void setPropagazioneAoo(Boolean propagazioneAoo) {
		this.propagazioneAoo = propagazioneAoo;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModelloCampo modelloCampo = (ModelloCampo) o;

        if ( ! Objects.equals(id, modelloCampo.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ModelloCampo{" +
                "id=" + id +
                ", codice='" + codice + "'" +
                ", titolo='" + titolo + "'" +
                ", tipoCampo='" + tipoCampo + "'" +
                '}';
    }
}

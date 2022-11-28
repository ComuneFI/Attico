package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Avanzamento.
 */
@Entity
@Table(name = "AVANZAMENTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Avanzamento extends AbstractAuditingEntity implements Serializable {
	
	private static final long serialVersionUID = 4922146505359946748L;

	public Avanzamento() {
	}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_attivita",updatable=false)
    private DateTime dataAttivita = new DateTime();

    @Column(name = "note")
    private String note;
    
    @Column(name = "warning_type")
    private String warningType;
    
    @ManyToOne
    @JoinColumn(updatable=false)
    private SistemaAccreditato sistemaAccreditato;
    
    @ManyToOne
    @JoinColumn(updatable=false)
    private Atto atto;

    @Column(name = "stato")
    private String stato;

    @ManyToOne
    @JoinColumn(updatable=false)
    private Profilo profilo;

    @ManyToOne
    @JoinColumn(updatable=false)
    private Resoconto resoconto;

    @ManyToOne
    @JoinColumn(updatable=false)
    private Lettera lettera;

    @ManyToOne
    @JoinColumn(updatable=false)
    private Parere parere;

    @ManyToOne
    @JoinColumn(updatable=false)
    private Verbale verbale;


    @Column(name = "attivita",updatable=false)
    private String attivita;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

	public DateTime getDataAttivita() {
        return dataAttivita;
    }

    public void setDataAttivita(final DateTime dataAttivita) {
        this.dataAttivita = dataAttivita;
    }

    public String getNote() {
        return note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    public Atto getAtto() {
        return atto;
    }

    public void setAtto(final Atto atto) {
        this.atto = atto;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(final String stato) {
        this.stato = stato;
    }

    public Profilo getProfilo() {
        return profilo;
    }

    public void setProfilo(final Profilo profilo) {
        this.profilo = profilo;
    }

    public Resoconto getResoconto() {
        return resoconto;
    }

    public void setResoconto(final Resoconto resoconto) {
        this.resoconto = resoconto;
    }

    public Lettera getLettera() {
        return lettera;
    }

    public void setLettera(final Lettera lettera) {
        this.lettera = lettera;
    }

    public Parere getParere() {
        return parere;
    }

    public void setParere(final Parere parere) {
        this.parere = parere;
    }

    public Verbale getVerbale() {
        return verbale;
    }

    public void setVerbale(final Verbale verbale) {
        this.verbale = verbale;
    }

  /*  public OrdineGiorno getOrdineGiorno() {
        return ordineGiorno;
    }

    public void setOrdineGiorno(final OrdineGiorno ordineGiorno) {
        this.ordineGiorno = ordineGiorno;
    }

*/
    public String getAttivita() {
		return attivita;
	}

	public void setAttivita(final String attivita) {
		this.attivita = attivita;
	}

	public SistemaAccreditato getSistemaAccreditato() {
		return sistemaAccreditato;
	}

	public void setSistemaAccreditato(SistemaAccreditato sistemaAccreditato) {
		this.sistemaAccreditato = sistemaAccreditato;
	}

	public String getWarningType() {
		return warningType;
	}

	public void setWarningType(String warningType) {
		this.warningType = warningType;
	}

	@Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Avanzamento avanzamento = (Avanzamento) o;

        if ( ! Objects.equals(id, avanzamento.id)) {
			return false;
		}

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Avanzamento{" +
                "id=" + id +
                ", dataAttivita='" + dataAttivita + "'" +
                ", note='" + note + "'" +
                '}';
    }
}

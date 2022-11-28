package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A RichiestaHD.
 */
@Entity
@Table(name = "RICHIESTAHD")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RichiestaHD implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name="allegatoid")
	private Long allegatoId;
	
	@Column(name="aooid", insertable = true, updatable = false)
	private Long aooId;
	
	@Transient
    @JsonProperty
    private Aoo aoo;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_invio", nullable = false, updatable = false)
    private DateTime dataInvio;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_sospensione", nullable = true)
    private DateTime dataSospensione;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_chiusura", nullable = true)
    private DateTime dataChiusura;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_presa_visione_op", nullable = true)
    private DateTime dataPresaVisione;

    @Size(max = 200)
    @Column(name = "oggetto", length = 200)
    private String oggetto;

    @Size(max = 2500)
    @Column(name = "testo_richiesta", length = 2500)
    private String testoRichiesta;

    @ManyToOne
    @JoinColumn(name="autore_id",insertable=true,updatable=false)
    private Utente autore;

    @ManyToOne
    @JoinColumn(name="stato_id",insertable=true,updatable=true)
    private StatoRichiestaHD stato;
    
    @OneToMany(mappedBy="richiesta")
    @OrderBy(value = " data_invio DESC")
    private Set<RispostaHD> risposte = new HashSet<RispostaHD>();

    @ManyToOne
    private TipoRichiestaHD tipo;
    
    @Transient
    @JsonProperty
    private int nRisposte;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(DateTime dataInvio) {
        this.dataInvio = dataInvio;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getTestoRichiesta() {
        return testoRichiesta;
    }

    public void setTestoRichiesta(String testoRichiesta) {
        this.testoRichiesta = testoRichiesta;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente profilo) {
        this.autore = profilo;
    }

    public StatoRichiestaHD getStato() {
        return stato;
    }

    public void setStato(StatoRichiestaHD statoRichiestaHD) {
        this.stato = statoRichiestaHD;
    }

    public TipoRichiestaHD getTipo() {
        return tipo;
    }

    public void setTipo(TipoRichiestaHD tipoRichiestaHD) {
        this.tipo = tipoRichiestaHD;
    }

	public DateTime getDataPresaVisione() {
		return dataPresaVisione;
	}

	public void setDataPresaVisione(DateTime dataPresaVisione) {
		this.dataPresaVisione = dataPresaVisione;
	}

	public Set<RispostaHD> getRisposte() {
		return risposte;
	}

	public void setRisposte(Set<RispostaHD> risposte) {
		this.risposte = risposte;
	}

	public int getnRisposte() {
		return nRisposte;
	}

	public void setnRisposte(int nRisposte) {
		this.nRisposte = nRisposte;
	}

	public DateTime getDataSospensione() {
		return dataSospensione;
	}

	public void setDataSospensione(DateTime dataSospensione) {
		this.dataSospensione = dataSospensione;
	}

	public DateTime getDataChiusura() {
		return dataChiusura;
	}

	public void setDataChiusura(DateTime dataChiusura) {
		this.dataChiusura = dataChiusura;
	}

	public Long getAllegatoId() {
		return allegatoId;
	}

	public void setAllegatoId(Long allegatoId) {
		this.allegatoId = allegatoId;
	}

	public Long getAooId() {
		return aooId;
	}

	public void setAooId(Long aooId) {
		this.aooId = aooId;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((autore == null) ? 0 : autore.hashCode());
		result = prime * result
				+ ((dataInvio == null) ? 0 : dataInvio.hashCode());
		result = prime
				* result
				+ ((dataPresaVisione == null) ? 0 : dataPresaVisione.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((oggetto == null) ? 0 : oggetto.hashCode());
		result = prime * result + ((stato == null) ? 0 : stato.hashCode());
		result = prime * result
				+ ((testoRichiesta == null) ? 0 : testoRichiesta.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RichiestaHD other = (RichiestaHD) obj;
		if (autore == null) {
			if (other.autore != null)
				return false;
		} else if (!autore.equals(other.autore))
			return false;
		if (dataInvio == null) {
			if (other.dataInvio != null)
				return false;
		} else if (!dataInvio.equals(other.dataInvio))
			return false;
		if (dataPresaVisione == null) {
			if (other.dataPresaVisione != null)
				return false;
		} else if (!dataPresaVisione.equals(other.dataPresaVisione))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (oggetto == null) {
			if (other.oggetto != null)
				return false;
		} else if (!oggetto.equals(other.oggetto))
			return false;
		if (risposte == null) {
			if (other.risposte != null)
				return false;
		} else if (!risposte.equals(other.risposte))
			return false;
		if (stato == null) {
			if (other.stato != null)
				return false;
		} else if (!stato.equals(other.stato))
			return false;
		if (testoRichiesta == null) {
			if (other.testoRichiesta != null)
				return false;
		} else if (!testoRichiesta.equals(other.testoRichiesta))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		return true;
	}
}

package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A RichiestaHD.
 */
@Entity
@Table(name = "RISPOSTAHD")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RispostaHD implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreatedDate
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_invio", nullable = false)
    private DateTime dataInvio;
    
    @ManyToOne
    @JoinColumn(name="operatore_id",insertable=true,updatable=false)
    private Utente operatore;
    
    @Size(max = 2500)
    @Column(name = "testo_risposta", length = 2500)
    private String testoRisposta;
    
    @ManyToOne
    @JoinColumn(name="richiestahd_id",insertable=true,updatable=false)
    private RichiestaHD richiesta;

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

	public Utente getOperatore() {
		return operatore;
	}

	public void setOperatore(Utente operatore) {
		this.operatore = operatore;
	}

	public String getTestoRisposta() {
		return testoRisposta;
	}

	public void setTestoRisposta(String testoRisposta) {
		this.testoRisposta = testoRisposta;
	}

	public RichiestaHD getRichiesta() {
		return richiesta;
	}

	public void setRichiesta(RichiestaHD richiesta) {
		this.richiesta = richiesta;
	}

	@Override
	public String toString() {
		return "RispostaHD [id=" + id + ", dataInvio=" + dataInvio
				+ ", operatore=" + operatore + ", testoRisposta="
				+ testoRisposta + ", richiesta=" + richiesta + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataInvio == null) ? 0 : dataInvio.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((operatore == null) ? 0 : operatore.hashCode());
		result = prime * result
				+ ((richiesta == null) ? 0 : richiesta.hashCode());
		result = prime * result
				+ ((testoRisposta == null) ? 0 : testoRisposta.hashCode());
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
		RispostaHD other = (RispostaHD) obj;
		if (dataInvio == null) {
			if (other.dataInvio != null)
				return false;
		} else if (!dataInvio.equals(other.dataInvio))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (operatore == null) {
			if (other.operatore != null)
				return false;
		} else if (!operatore.equals(other.operatore))
			return false;
		if (richiesta == null) {
			if (other.richiesta != null)
				return false;
		} else if (!richiesta.equals(other.richiesta))
			return false;
		if (testoRisposta == null) {
			if (other.testoRisposta != null)
				return false;
		} else if (!testoRisposta.equals(other.testoRisposta))
			return false;
		return true;
	}

}

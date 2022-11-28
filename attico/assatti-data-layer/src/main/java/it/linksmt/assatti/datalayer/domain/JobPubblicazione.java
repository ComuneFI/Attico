package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A JobPubblicazione.
 */
@Entity
@Table(name = "job_pubblicazione")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

public class JobPubblicazione extends AbstractAuditingEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "stato")
	private StatoJob stato;

	@Column(name = "tentativi")
	private Integer tentativi;

	@Column(name = "numero_protocollo")
	private String numeroProtocollo;

	@Column(name = "data_protocollo")
	private String dataProtollo;

	@Column(name = "segnatura")
	private String segnatura;

	@Column(name = "dettaglio_errore", length = 1000)
	private String dettaglioErrore;

	@ManyToOne
	@JoinColumn(name = "atto_id", insertable = true, updatable = false)
	private Atto atto;

	public Long getId() {
		return id;
	}

	public StatoJob getStato() {
		return stato;
	}

	public Integer getTentativi() {
		return tentativi;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setStato(StatoJob stato) {
		this.stato = stato;
	}

	public void setTentativi(Integer tentativi) {
		this.tentativi = tentativi;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public String getDataProtollo() {
		return dataProtollo;
	}

	public void setDataProtollo(String dataProtollo) {
		this.dataProtollo = dataProtollo;
	}

	public String getSegnatura() {
		return segnatura;
	}

	public void setSegnatura(String segnatura) {
		this.segnatura = segnatura;
	}

	public String getDettaglioErrore() {
		return dettaglioErrore;
	}

	public void setDettaglioErrore(String dettaglioErrore) {
		this.dettaglioErrore = dettaglioErrore;
	}

}

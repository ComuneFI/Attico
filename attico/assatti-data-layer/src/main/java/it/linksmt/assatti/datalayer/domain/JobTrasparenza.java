package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Date;

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
 * A JobTrasparenza.
 */
@Entity
@Table(name = "job_trasparenza")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class JobTrasparenza extends AbstractAuditingEntity implements Serializable {

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

	@Column(name = "segnatura", columnDefinition = "TEXT")
	private String segnatura;

	@Column(name = "dettaglio_errore", length = 1000)
	private String dettaglioErrore;

	@Column(name = "modified_by", length = 50)
	private String modifiedBy;

	@Column(name = "modified_date")
	private Date modifiedDate;

	@Column(name = "data_invio_trasparenza")
	private Date dataInvioTrasparenza;

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

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the dataInvioTrasparenza
	 */
	public Date getDataInvioTrasparenza() {
		return dataInvioTrasparenza;
	}

	/**
	 * @param dataInvioTrasparenza the dataInvioTrasparenza to set
	 */
	public void setDataInvioTrasparenza(Date dataInvioTrasparenza) {
		this.dataInvioTrasparenza = dataInvioTrasparenza;
	}

}

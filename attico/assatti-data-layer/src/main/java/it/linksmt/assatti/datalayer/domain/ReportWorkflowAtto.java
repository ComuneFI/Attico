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
 * A ReportWorkflowAtto.
 */
@Entity
@Table(name = "report_workflow_atto")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	    
public class ReportWorkflowAtto extends AbstractAuditingEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name="atto_id", insertable = true, updatable = false)
    private Atto atto;
	
	@Enumerated(EnumType.STRING)
	@Column(name="stato")
	private StatoReportWorkflowAtto stato;

  	@Column(name="errore")
  	private String errore;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public StatoReportWorkflowAtto getStato() {
		return stato;
	}

	public void setStato(StatoReportWorkflowAtto stato) {
		this.stato = stato;
	}

	public String getErrore() {
		return errore;
	}

	public void setErrore(String errore) {
		this.errore = errore;
	}
	
	

    
}

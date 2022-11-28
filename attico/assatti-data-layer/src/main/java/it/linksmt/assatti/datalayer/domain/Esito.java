package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Esito.
 */
@Entity
@Table(name = "ESITO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Esito implements Serializable {
	
	public Esito() {
		
	}
	
	@Id
	private String id;
	
	@Column(name = "label")
	private String label;
	
	@Column(name = "label_document")
	private String labelDocument;
	
	@Column(name = "tipiatto")
	private String tipiAtto;
	
	@Column(name = "registra_votazione")
    private Boolean registraVotazione;
	
	@Column(name = "visibilita_presenza")
    private Boolean visibilitaPresenza;
	
	@Column(name = "visibilita_votazione")
    private Boolean visibilitaVotazione;
	
	@Column(name = "ammette_ie")
    private Boolean ammetteIE;
	
	@Column(name = "organo")
	private String organo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getTipiAtto() {
		return tipiAtto;
	}

	public void setTipiAtto(String tipiAtto) {
		this.tipiAtto = tipiAtto;
	}

	public Boolean getRegistraVotazione() {
		return registraVotazione;
	}

	public void setRegistraVotazione(Boolean registraVotazione) {
		this.registraVotazione = registraVotazione;
	}

	public Boolean getVisibilitaPresenza() {
		return visibilitaPresenza;
	}

	public void setVisibilitaPresenza(Boolean visibilitaPresenza) {
		this.visibilitaPresenza = visibilitaPresenza;
	}

	public Boolean getVisibilitaVotazione() {
		return visibilitaVotazione;
	}

	public void setVisibilitaVotazione(Boolean visibilitaVotazione) {
		this.visibilitaVotazione = visibilitaVotazione;
	}

	public Boolean getAmmetteIE() {
		return ammetteIE;
	}

	public void setAmmetteIE(Boolean ammetteIE) {
		this.ammetteIE = ammetteIE;
	}

	public String getLabelDocument() {
		return labelDocument;
	}

	public void setLabelDocument(String labelDocument) {
		this.labelDocument = labelDocument;
	}

	public String getOrgano() {
		return organo;
	}

	public void setOrgano(String organo) {
		this.organo = organo;
	}
	

}

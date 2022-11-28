package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A ComponentiGiunta
 */
@Entity
@Table(name = "COMPONENTIGIUNTA" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ComponentiGiunta extends AbstractAuditingEntity implements Serializable {

	public ComponentiGiunta(final Long id) {
		this.id = id;
	}
	
	public ComponentiGiunta() {
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "presente")
	private Boolean presente;
	
	@Column(name = "presente_ie")
	private Boolean presenteIE;
	
	@ManyToOne
	@JoinColumn(name="odg_id")
    private OrdineGiorno ordineGiorno;
	
	@ManyToOne
	@JoinColumn(name="atto_id")
    private AttiOdg atto;
	
	@ManyToOne
	@JoinColumn(name="profilo_id")
    private Profilo profilo;
	
	@Column(name = "is_presidente_inizio")
	private Boolean isPresidenteInizio;
	
	@Column(name = "is_presidente_fine")
	private Boolean isPresidenteFine;
	
	@Column(name = "is_segretario_inizio")
	private Boolean isSegretarioInizio;
	
	@Column(name = "is_segretario_fine")
	private Boolean isSegretarioFine;
	
	@Column(name = "is_scrutatore")
	private Boolean isScrutatore;
	
	@Column(name = "votazione")
	private String votazione;
	
	@Column(name = "votazione_ie")
	private String votazioneIE;
	
	@ManyToOne
	@JoinColumn(name="qualificaprofessionale_id")
    private QualificaProfessionale qualificaProfessionale;
	
	@Column(name = "is_presidente_ie")
	private Boolean isPresidenteIE;
	
	@Column(name = "is_segretario_ie")
	private Boolean isSegretarioIE;
	
	@Column(name = "is_scrutatore_ie")
	private Boolean isScrutatoreIE;
	
	@Column(name = "is_sindaco")
	private Boolean isSindaco;
	
	@ManyToOne
	@JoinColumn(name="qualificaprofessionale_ie_id")
    private QualificaProfessionale qualificaProfessionaleIE;
	
	
	@Transient
	@JsonProperty
	private Boolean isVicePresidenteRegione;
	
	@Transient
	@JsonProperty
	private Boolean isPresidenteRegione;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getPresente() {
		return presente;
	}

	public void setPresente(Boolean presente) {
		this.presente = presente;
	}

	public OrdineGiorno getOrdineGiorno() {
		return ordineGiorno;
	}

	public void setOrdineGiorno(OrdineGiorno ordineGiorno) {
		this.ordineGiorno = ordineGiorno;
	}

	public AttiOdg getAtto() {
		return atto;
	}

	public void setAtto(AttiOdg atto) {
		this.atto = atto;
	}

	public Profilo getProfilo() {
		return profilo;
	}

	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}

	public Boolean getIsPresidenteInizio() {
		return isPresidenteInizio;
	}

	public void setIsPresidenteInizio(Boolean isPresidenteInizio) {
		this.isPresidenteInizio = isPresidenteInizio;
	}

	public Boolean getIsPresidenteFine() {
		return isPresidenteFine;
	}

	public void setIsPresidenteFine(Boolean isPresidenteFine) {
		this.isPresidenteFine = isPresidenteFine;
	}

	public Boolean getIsSegretarioInizio() {
		return isSegretarioInizio;
	}

	public void setIsSegretarioInizio(Boolean isSegretarioInizio) {
		this.isSegretarioInizio = isSegretarioInizio;
	}

	public Boolean getIsSegretarioFine() {
		return isSegretarioFine;
	}

	public void setIsSegretarioFine(Boolean isSegretarioFine) {
		this.isSegretarioFine = isSegretarioFine;
	}

	public QualificaProfessionale getQualificaProfessionale() {
		return qualificaProfessionale;
	}

	public void setQualificaProfessionale(QualificaProfessionale qualificaProfessionale) {
		this.qualificaProfessionale = qualificaProfessionale;
	}

	public Boolean getIsVicePresidenteRegione() {
		return isVicePresidenteRegione;
	}

	public Boolean getIsPresidenteRegione() {
		return isPresidenteRegione;
	}

	public void setIsVicePresidenteRegione(Boolean isVicePresidenteRegione) {
		this.isVicePresidenteRegione = isVicePresidenteRegione;
	}

	public void setIsPresidenteRegione(Boolean isPresidenteRegione) {
		this.isPresidenteRegione = isPresidenteRegione;
	}

	public Boolean getIsScrutatore() {
		return isScrutatore;
	}

	public void setIsScrutatore(Boolean isScrutatore) {
		this.isScrutatore = isScrutatore;
	}

	public String getVotazione() {
		return votazione;
	}

	public void setVotazione(String votazione) {
		this.votazione = votazione;
	}
	
	public Boolean getPresenteIE() {
		return presenteIE;
	}

	public void setPresenteIE(Boolean presenteIE) {
		this.presenteIE = presenteIE;
	}

	public String getVotazioneIE() {
		return votazioneIE;
	}

	public void setVotazioneIE(String votazioneIE) {
		this.votazioneIE = votazioneIE;
	}

	public Boolean getIsPresidenteIE() {
		return isPresidenteIE;
	}

	public void setIsPresidenteIE(Boolean isPresidenteIE) {
		this.isPresidenteIE = isPresidenteIE;
	}

	public Boolean getIsSegretarioIE() {
		return isSegretarioIE;
	}

	public void setIsSegretarioIE(Boolean isSegretarioIE) {
		this.isSegretarioIE = isSegretarioIE;
	}

	public Boolean getIsScrutatoreIE() {
		return isScrutatoreIE;
	}

	public void setIsScrutatoreIE(Boolean isScrutatoreIE) {
		this.isScrutatoreIE = isScrutatoreIE;
	}
	
	public Boolean getIsSindaco() {
		return isSindaco;
	}

	public void setIsSindaco(Boolean isSindaco) {
		this.isSindaco = isSindaco;
	}

	public QualificaProfessionale getQualificaProfessionaleIE() {
		return qualificaProfessionaleIE;
	}

	public void setQualificaProfessionaleIE(QualificaProfessionale qualificaProfessionaleIE) {
		this.qualificaProfessionaleIE = qualificaProfessionaleIE;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ComponentiGiunta){
			ComponentiGiunta cg = (ComponentiGiunta) obj;
			
			if (this.isPresidenteInizio == cg.getIsPresidenteInizio() &&
				this.isSegretarioFine == cg.getIsSegretarioFine() &&
				this.profilo.getUtente().getUsername().equalsIgnoreCase(cg.getProfilo().getUtente().getUsername()))
				return true;
			else
				return false;
		} else 
			return super.equals(obj);
	}
}

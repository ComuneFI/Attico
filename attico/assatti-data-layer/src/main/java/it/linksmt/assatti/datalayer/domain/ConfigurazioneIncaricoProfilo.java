package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A ConfigurazioneIncaricoProfilo.
 */
@Entity
@Table(name = "configurazione_incarico_profilo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class ConfigurazioneIncaricoProfilo implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ConfigurazioneIncaricoProfiloId primaryKey = new ConfigurazioneIncaricoProfiloId();
	
	@Column(name = "ordine_firma")
	private Integer ordineFirma;
	
	@CreatedDate
    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "data_creazione", nullable = false, updatable=false)
    private DateTime dataCreazione = DateTime.now();
	
	@ManyToOne
	private QualificaProfessionale qualificaProfessionale;
	
	/**
	 * Sara' diverso da null soltanto se l'atto risulta ancora in itinere (fineiter_date non valorizzato)
	 */
	@JsonIgnore
    @Basic(fetch=FetchType.LAZY)
    @Formula("(select a.id from configurazione_incarico ci inner join configurazione_incarico_profilo cip on ci.id = cip.id_configurazione_incarico inner join atto a on a.id = ci.atto_id where cip.id_configurazione_incarico = id_configurazione_incarico and cip.id_profilo = id_profilo and a.fineiter_date is null)")
	private Long attoIdItinere; 
	
	@Column(name = "giorni_scadenza")
	private Integer giorniScadenza;
	
	@Column(name = "reassigned")
	private Boolean reassigned;

	/*
	 * Get & Set
	 */

	public Boolean getReassigned() {
		return reassigned;
	}

	public void setReassigned(Boolean reassigned) {
		this.reassigned = reassigned;
	}

	public ConfigurazioneIncaricoProfiloId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(ConfigurazioneIncaricoProfiloId primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Integer getOrdineFirma() {
		return ordineFirma;
	}

	public void setOrdineFirma(Integer ordineFirma) {
		this.ordineFirma = ordineFirma;
	}

	public QualificaProfessionale getQualificaProfessionale() {
		return qualificaProfessionale;
	}

	public void setQualificaProfessionale(QualificaProfessionale qualificaProfessionale) {
		this.qualificaProfessionale = qualificaProfessionale;
	}

	public Integer getGiorniScadenza() {
		return giorniScadenza;
	}

	public void setGiorniScadenza(Integer giorniScadenza) {
		this.giorniScadenza = giorniScadenza;
	}

	public DateTime getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(DateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Long getAttoIdItinere() {
		return attoIdItinere;
	}

	public void setAttoIdItinere(Long attoIdItinere) {
		this.attoIdItinere = attoIdItinere;
	}
}

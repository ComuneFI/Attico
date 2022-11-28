package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

/**
 * A ConfigurazioneIncaricoAoo.
 */
@Entity
@Table(name = "configurazione_incarico_aoo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConfigurazioneIncaricoAoo implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ConfigurazioneIncaricoAooId primaryKey = new ConfigurazioneIncaricoAooId();

	@CreatedDate
    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "data_creazione", nullable = false, updatable=false)
    private DateTime dataCreazione = DateTime.now();
	
	@Column(name = "ordine_firma")
	private Integer ordineFirma;
	
	@ManyToOne
	private QualificaProfessionale qualificaProfessionale;
	
	@Column(name = "giorni_scadenza")
	private Integer giorniScadenza;
	
	
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "data_manuale")
    private DateTime dataManuale;

	/*
	 * Get & Set
	 */

	public ConfigurazioneIncaricoAooId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(ConfigurazioneIncaricoAooId primaryKey) {
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

	public DateTime getDataManuale() {
		return dataManuale;
	}

	public void setDataManuale(DateTime dataManuale) {
		this.dataManuale = dataManuale;
	}

}

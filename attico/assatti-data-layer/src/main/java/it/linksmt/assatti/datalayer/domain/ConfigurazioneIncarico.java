package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Date;

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

/**
 * Model class for ConfigurazioneIncarico.
 */
@Entity
@Table(name = "configurazione_incarico")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConfigurazioneIncarico implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "tipologia")
	private Integer tipologia;

	@Column(name = "enabled")
	private Boolean enabled;

	@Column(name = "editor")
	private Boolean editor;
	
	@Column(name = "data_creazione")
	private Date dataCreazione;

	@Column(name = "data_modifica")
	private Date dataModifica;

	// @Transient
	// @JsonProperty
	// private Aoo aooNonProponente;

	@ManyToOne
	@JoinColumn(name = "atto_id")
	private Atto atto;

	@ManyToOne
	@JoinColumn(name = "id_configurazione_task")
	private ConfigurazioneTask configurazioneTask;



//	public ConfigurazioneIncarico() {
//		this.enabled = true;
//	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atto == null) ? 0 : atto.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * Get & Set
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getEditor() {
		return editor;
	}

	public void setEditor(Boolean editor) {
		this.editor = editor;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public Integer getTipologia() {
		return tipologia;
	}

	public void setTipologia(Integer tipologia) {
		this.tipologia = tipologia;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public ConfigurazioneTask getConfigurazioneTask() {
		return configurazioneTask;
	}

	public void setConfigurazioneTask(ConfigurazioneTask configurazioneTask) {
		this.configurazioneTask = configurazioneTask;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}
}

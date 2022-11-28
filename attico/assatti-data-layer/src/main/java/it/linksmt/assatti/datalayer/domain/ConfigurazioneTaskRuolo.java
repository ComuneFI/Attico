package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ConfigurazioneTaskRuolo.
 */
@Entity
@Table(name = "configurazione_task_ruolo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConfigurazioneTaskRuolo implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ConfigurazioneTaskRuoloId primaryKey = new ConfigurazioneTaskRuoloId();


	/*
	 * Get & Set
	 */

	public ConfigurazioneTaskRuoloId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(ConfigurazioneTaskRuoloId primaryKey) {
		this.primaryKey = primaryKey;
	}

}

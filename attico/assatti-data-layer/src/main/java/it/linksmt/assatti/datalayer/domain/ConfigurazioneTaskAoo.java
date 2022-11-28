package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ConfigurazioneTaskAoo.
 */
@Entity
@Table(name = "configurazione_task_aoo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConfigurazioneTaskAoo implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ConfigurazioneTaskAooId primaryKey = new ConfigurazioneTaskAooId();


	/*
	 * Get & Set
	 */

	public ConfigurazioneTaskAooId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(ConfigurazioneTaskAooId primaryKey) {
		this.primaryKey = primaryKey;
	}

}

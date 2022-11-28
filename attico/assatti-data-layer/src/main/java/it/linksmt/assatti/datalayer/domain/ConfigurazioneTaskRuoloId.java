package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ConfigurazioneTaskRuoloId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="id_configurazione_task", nullable=false)
	private Long idConfigurazioneTask;

	@Column(name="id_ruolo", nullable=false)
	private Long idRuolo;
	
	public int hashCode() {
		if(idConfigurazioneTask==null && idRuolo!=null) {
			return (int) (idRuolo.hashCode());
		} else if(idConfigurazioneTask!=null && idRuolo==null) {
			return (int) (idConfigurazioneTask.hashCode());
		} else if(idConfigurazioneTask==null && idRuolo==null) {
			return 0;
		}
		return (int) (idConfigurazioneTask.hashCode() + idRuolo.hashCode());
	}

	/*
	 * Get & Set
	 */
	
	public Long getIdConfigurazioneTask() {
		return idConfigurazioneTask;
	}

	public void setIdConfigurazioneTask(Long idConfigurazioneTask) {
		this.idConfigurazioneTask = idConfigurazioneTask;
	}

	public Long getIdRuolo() {
		return idRuolo;
	}

	public void setIdRuolo(Long idRuolo) {
		this.idRuolo = idRuolo;
	}
	
	
}

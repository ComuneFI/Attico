package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ConfigurazioneTaskAooId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="id_configurazione_task", nullable=false)
	private Long idConfigurazioneTask;

	@Column(name="id_aoo", nullable=false)
	private Long idAoo;
	
	public int hashCode() {
		if(idConfigurazioneTask==null && idAoo!=null) {
			return (int) (idAoo.hashCode());
		} else if(idConfigurazioneTask!=null && idAoo==null) {
			return (int) (idConfigurazioneTask.hashCode());
		} else if(idConfigurazioneTask==null && idAoo==null) {
			return 0;
		}
		return (int) (idConfigurazioneTask.hashCode() + idAoo.hashCode());
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

	public Long getIdAoo() {
		return idAoo;
	}

	public void setIdAoo(Long idAoo) {
		this.idAoo = idAoo;
	}
	
	
}

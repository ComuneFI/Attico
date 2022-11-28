package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ConfigurazioneIncaricoAooId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="id_configurazione_incarico", nullable=false)
	private Long idConfigurazioneIncarico;

	@Column(name="id_aoo", nullable=false)
	private Long idAoo;
	
	public int hashCode() {
		if(idConfigurazioneIncarico==null && idAoo!=null) {
			return (int) (idAoo.hashCode());
		} else if(idConfigurazioneIncarico!=null && idAoo==null) {
			return (int) (idConfigurazioneIncarico.hashCode());
		} else if(idConfigurazioneIncarico==null && idAoo==null) {
			return 0;
		}
		return (int) (idConfigurazioneIncarico.hashCode() + idAoo.hashCode());
	}

	/*
	 * Get & Set
	 */
	
	public Long getIdConfigurazioneIncarico() {
		return idConfigurazioneIncarico;
	}

	public void setIdConfigurazioneIncarico(Long idConfigurazioneIncarico) {
		this.idConfigurazioneIncarico = idConfigurazioneIncarico;
	}

	public Long getIdAoo() {
		return idAoo;
	}

	public void setIdAoo(Long idAoo) {
		this.idAoo = idAoo;
	}
	
	
}

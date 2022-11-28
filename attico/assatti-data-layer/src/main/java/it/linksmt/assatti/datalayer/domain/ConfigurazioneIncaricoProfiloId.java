package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ConfigurazioneIncaricoProfiloId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public ConfigurazioneIncaricoProfiloId() {
		super();
	}
	
	public ConfigurazioneIncaricoProfiloId(Long idConfigurazioneIncarico, Long idProfilo) {
		super();
		this.idConfigurazioneIncarico = idConfigurazioneIncarico;
		this.idProfilo = idProfilo;
	}

	@Column(name="id_configurazione_incarico", nullable=false)
	private Long idConfigurazioneIncarico;

	@Column(name="id_profilo", nullable=false)
	private Long idProfilo;
	
	public int hashCode() {
		if(idConfigurazioneIncarico==null && idProfilo!=null) {
			return (int) (idProfilo.hashCode());
		} else if(idConfigurazioneIncarico!=null && idProfilo==null) {
			return (int) (idConfigurazioneIncarico.hashCode());
		} else if(idConfigurazioneIncarico==null && idProfilo==null) {
			return 0;
		}
		return (int) (idConfigurazioneIncarico.hashCode() + idProfilo.hashCode());
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

	public Long getIdProfilo() {
		return idProfilo;
	}

	public void setIdProfilo(Long idProfilo) {
		this.idProfilo = idProfilo;
	}

	@Override
	public String toString() {
		return "ConfigurazioneIncaricoProfiloId [idConfigurazioneIncarico=" + idConfigurazioneIncarico + ", idProfilo="
				+ idProfilo + "]";
	}
	
	
}

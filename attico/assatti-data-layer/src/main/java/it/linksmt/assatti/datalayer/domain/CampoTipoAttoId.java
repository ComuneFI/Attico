package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CampoTipoAttoId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="tipoatto_id", nullable=false)
	private Long idTipoAtto;

	@Column(name="campo_id", nullable=false)
	private Long idCampo;
	
	public int hashCode() {
		if(idTipoAtto==null && idCampo!=null) {
			return (int) (idCampo.hashCode());
		} else if(idTipoAtto!=null && idCampo==null) {
			return (int) (idTipoAtto.hashCode());
		} else if(idTipoAtto==null && idCampo==null) {
			return 0;
		}
		return (int) (idTipoAtto.hashCode() + idCampo.hashCode());
	}
	
	/*
	 * Get & Set
	 */

	public Long getIdTipoAtto() {
		return idTipoAtto;
	}

	public Long getIdCampo() {
		return idCampo;
	}

	public void setIdCampo(Long idCampo) {
		this.idCampo = idCampo;
	}

	public void setIdTipoAtto(Long idTipoAtto) {
		this.idTipoAtto = idTipoAtto;
	}

	
}

package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TipoDeterminazioneTipoAttoId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="tipoatto_id", nullable=false)
	private Long idTipoAtto;

	@Column(name="tipodeterminazione_id", nullable=false)
	private Long idTipoDeterminazione;
	
	public int hashCode() {
		if(idTipoAtto==null && idTipoDeterminazione!=null) {
			return (int) (idTipoDeterminazione.hashCode());
		} else if(idTipoAtto!=null && idTipoDeterminazione==null) {
			return (int) (idTipoAtto.hashCode());
		} else if(idTipoAtto==null && idTipoDeterminazione==null) {
			return 0;
		}
		return (int) (idTipoAtto.hashCode() + idTipoDeterminazione.hashCode());
	}
	
	/*
	 * Get & Set
	 */

	public Long getIdTipoAtto() {
		return idTipoAtto;
	}

	public Long getIdTipoDeterminazione() {
		return idTipoDeterminazione;
	}

	public void setIdTipoDeterminazione(Long idTipoDeterminazione) {
		this.idTipoDeterminazione = idTipoDeterminazione;
	}

	public void setIdTipoAtto(Long idTipoAtto) {
		this.idTipoAtto = idTipoAtto;
	}

	
}

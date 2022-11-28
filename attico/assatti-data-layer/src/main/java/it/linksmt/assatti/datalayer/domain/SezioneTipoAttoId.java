package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SezioneTipoAttoId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="tipoatto_id", nullable=false)
	private Long idTipoAtto;

	@Column(name="sezione_id", nullable=false)
	private Long idSezione;
	
	public int hashCode() {
		if(idTipoAtto==null && idSezione!=null) {
			return (int) (idSezione.hashCode());
		} else if(idTipoAtto!=null && idSezione==null) {
			return (int) (idTipoAtto.hashCode());
		} else if(idTipoAtto==null && idSezione==null) {
			return 0;
		}
		return (int) (idTipoAtto.hashCode() + idSezione.hashCode());
	}
	
	/*
	 * Get & Set
	 */

	public Long getIdTipoAtto() {
		return idTipoAtto;
	}

	public void setIdTipoAtto(Long idTipoAtto) {
		this.idTipoAtto = idTipoAtto;
	}

	public Long getIdSezione() {
		return idSezione;
	}

	public void setIdSezione(Long idSezione) {
		this.idSezione = idSezione;
	}
	
}

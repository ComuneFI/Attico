package it.linksmt.assatti.datalayer.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "tipodeterminazione_tipoatto")
public class TipoDeterminazioneTipoAtto {

	@EmbeddedId
	private TipoDeterminazioneTipoAttoId primaryKey = new TipoDeterminazioneTipoAttoId();

	@MapsId("idTipoAtto")
	@ManyToOne
	@JoinColumn(name = "tipoatto_id", insertable=false,updatable=false)
	private TipoAtto tipoAtto;

	@MapsId("idTipoDeterminazione")
	@ManyToOne
	@JoinColumn(name = "tipodeterminazione_id", insertable=false,updatable=false)
	private TipoDeterminazione tipoDeterminazione;
	
	public TipoDeterminazioneTipoAttoId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(TipoDeterminazioneTipoAttoId primaryKey) {
		this.primaryKey = primaryKey;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public TipoDeterminazione getTipoDeterminazione() {
		return tipoDeterminazione;
	}

	public void setTipoDeterminazione(TipoDeterminazione tipoDeterminazione) {
		this.tipoDeterminazione = tipoDeterminazione;
	}

}

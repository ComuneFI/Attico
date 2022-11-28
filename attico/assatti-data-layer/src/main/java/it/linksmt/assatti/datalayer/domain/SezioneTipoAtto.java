package it.linksmt.assatti.datalayer.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "sezione_tipoatto")
public class SezioneTipoAtto {

	@EmbeddedId
	private SezioneTipoAttoId primaryKey = new SezioneTipoAttoId();
	
	@Column(name="visibile")
	private boolean visibile;

	@MapsId("idTipoAtto")
	@ManyToOne
	@JoinColumn(name = "tipoatto_id", insertable=false,updatable=false)
	private TipoAtto tipoAtto;

	@MapsId("idSezione")
	@ManyToOne
	@JoinColumn(name = "sezione_id", insertable=false,updatable=false)
	private Sezione sezione;
	
	public SezioneTipoAttoId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(SezioneTipoAttoId primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isVisibile() {
		return visibile;
	}

	public void setVisibile(boolean visibile) {
		this.visibile = visibile;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public Sezione getSezione() {
		return sezione;
	}

	public void setSezione(Sezione sezione) {
		this.sezione = sezione;
	}
	
	
	
}

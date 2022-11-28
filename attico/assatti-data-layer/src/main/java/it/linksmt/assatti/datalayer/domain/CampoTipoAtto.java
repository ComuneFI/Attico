package it.linksmt.assatti.datalayer.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "campo_tipoatto")
public class CampoTipoAtto {

	@EmbeddedId
	private CampoTipoAttoId primaryKey = new CampoTipoAttoId();
	
	@Column(name="visibile")
	private boolean visibile;

	@MapsId("idTipoAtto")
	@ManyToOne
	@JoinColumn(name = "tipoatto_id", insertable=false,updatable=false)
	private TipoAtto tipoAtto;

	@MapsId("idCampo")
	@ManyToOne
	@JoinColumn(name = "campo_id", insertable=false,updatable=false)
	private Campo campo;
	
	public CampoTipoAttoId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(CampoTipoAttoId primaryKey) {
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

	public Campo getCampo() {
		return campo;
	}

	public void setCampo(Campo campo) {
		this.campo = campo;
	}

}

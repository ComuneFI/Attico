package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * A Evento.
 */
@Entity
@Table(name = "EVENTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Evento implements Serializable {

	public Evento() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "atto_id")
	private Atto atto;

	@ManyToOne
	@JoinColumn(name = "tipoEvento_id")
	private TipoEvento tipoEvento;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name = "data_creazione")
    private DateTime dataCreazione;
	
	@Column(name = "info_aggiuntive")
	private String infoAggiuntive;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public TipoEvento getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(TipoEvento tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public DateTime getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(DateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public String getInfoAggiuntive() {
		return infoAggiuntive;
	}

	public void setInfoAggiuntive(String infoAggiuntive) {
		this.infoAggiuntive = infoAggiuntive;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Evento tipoAtto = (Evento) o;

		if (!Objects.equals(id, tipoAtto.id)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		String attoId = "NULL";
		if (atto != null)
			attoId = String.valueOf(atto.getId());
		
		return "Evento{" + "id=" + id + ", atto='" + attoId + "'"
				+ ", tipoEvento='" + tipoEvento.getCodice() + "'" + '}';
	}
}

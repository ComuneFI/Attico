package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * An Instant Message.
 */
@Entity
@Table(name = "instant_message")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InstantMessage implements Serializable {

	private static final long serialVersionUID = -781312344234L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "time_invio")
	private LocalDate timeInvio;
	
	@Column(name = "testo")
	private String testo;
	
	@Column(name = "colore")
	private String colore;
	
	@Column(name = "username")
	private String username;

	public Long getId() {
		return id;
	}

	public LocalDate getTimeInvio() {
		return timeInvio;
	}

	public String getTesto() {
		return testo;
	}

	public String getColore() {
		return colore;
	}

	public String getUsername() {
		return username;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTimeInvio(LocalDate timeInvio) {
		this.timeInvio = timeInvio;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((colore == null) ? 0 : colore.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((testo == null) ? 0 : testo.hashCode());
		result = prime * result + ((timeInvio == null) ? 0 : timeInvio.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstantMessage other = (InstantMessage) obj;
		if (colore == null) {
			if (other.colore != null)
				return false;
		} else if (!colore.equals(other.colore))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (testo == null) {
			if (other.testo != null)
				return false;
		} else if (!testo.equals(other.testo))
			return false;
		if (timeInvio == null) {
			if (other.timeInvio != null)
				return false;
		} else if (!timeInvio.equals(other.timeInvio))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}

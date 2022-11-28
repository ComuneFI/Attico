package it.linksmt.assatti.datalayer.domain;

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
import org.joda.time.DateTime;

/**
 * monitoraggioaccesso entity
 */
@Entity
@Table(name = "monitoraggioaccesso")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MonitoraggioAccesso implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "eventdate", insertable=true, updatable=false)
    private DateTime data = DateTime.now();
	
	@Column(name="username", insertable=true, updatable=false)
	private String username;
	
	@Column(name="hostname", insertable=true, updatable=false)
	private String hostname;
	
	@Column(name="ip_address", insertable=true, updatable=false)
	private String ipAddress;
	
	@Column(name="status", insertable=true, updatable=false)
	private String status;
	
	@Column(name="dettaglio", insertable=true, updatable=false)
	private String dettaglio;

	public Long getId() {
		return id;
	}

	public DateTime getData() {
		return data;
	}

	public String getUsername() {
		return username;
	}

	public String getHostname() {
		return hostname;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getStatus() {
		return status;
	}

	public String getDettaglio() {
		return dettaglio;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setData(DateTime data) {
		this.data = data;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDettaglio(String dettaglio) {
		this.dettaglio = dettaglio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((dettaglio == null) ? 0 : dettaglio.hashCode());
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		MonitoraggioAccesso other = (MonitoraggioAccesso) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (dettaglio == null) {
			if (other.dettaglio != null)
				return false;
		} else if (!dettaglio.equals(other.dettaglio))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	
}

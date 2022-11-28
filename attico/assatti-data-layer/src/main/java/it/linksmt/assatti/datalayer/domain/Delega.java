package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

/**
 * A Delega.
 */
@Entity
@Table(name = "delega")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Delega implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_validita_inizio")
	private LocalDate dataValiditaInizio;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_validita_fine")
	private LocalDate dataValiditaFine;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_creazione")
	private LocalDate dataCreazione;
	
	@Column(name = "enabled")
    private Boolean enabled;

	@ManyToOne
	@JoinColumn(name="profilo_delegante_id", insertable = true, updatable = true)
	private Profilo profiloDelegante;
	
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "delega_profilo_delegato", joinColumns = @JoinColumn(name = "id_delega"), inverseJoinColumns = @JoinColumn(name = "id_profilo_delegato"))
	private Set<Profilo> delegati = new HashSet<>();
	
	/*
	 * Get & Set
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDataValiditaInizio() {
		return dataValiditaInizio;
	}

	public void setDataValiditaInizio(LocalDate dataValiditaInizio) {
		this.dataValiditaInizio = dataValiditaInizio;
	}

	public LocalDate getDataValiditaFine() {
		return dataValiditaFine;
	}

	public void setDataValiditaFine(LocalDate dataValiditaFine) {
		this.dataValiditaFine = dataValiditaFine;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Profilo getProfiloDelegante() {
		return profiloDelegante;
	}

	public void setProfiloDelegante(Profilo profiloDelegante) {
		this.profiloDelegante = profiloDelegante;
	}

	public LocalDate getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Set<Profilo> getDelegati() {
		return delegati;
	}

	public void setDelegati(Set<Profilo> delegati) {
		this.delegati = delegati;
	}

}

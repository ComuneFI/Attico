package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Profilo.
 */
@Entity
@Table(name = "PROFILO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Profilo implements Serializable,  Comparable<Profilo> {

	public Profilo(Long id) {
		this.id=id;
	}

	public Profilo() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "descrizione")
	private String descrizione;

	@Embedded
	private Validita validita = new Validita();

	@Column(name = "delega")
	private String delega;

	@Column(name="predefinito")
	private Boolean predefinito;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinTable(name = "PROFILOTIPOATTO", joinColumns = { 
			@JoinColumn(name = "profilo_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "tipoatto_id", 
					nullable = false, updatable = false) })
	private Set<TipoAtto> tipiAtto = new HashSet<TipoAtto>();

	@ManyToOne
	@JoinColumn(name = "utente_id")
	private Utente utente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="aoo_id",insertable=true,updatable=false)
	private Aoo aoo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "grupporuolo_id")
	private GruppoRuolo grupporuolo;

   	@ManyToMany(fetch = FetchType.EAGER)
   	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
   	@JoinTable(name = "PROFILO_HASQUALIFICA", joinColumns = { @JoinColumn(name = "profilo_id", nullable = false ) }, inverseJoinColumns = { @JoinColumn(name = "qualifica_id", nullable = false ) } )
   	@OrderBy(value = "denominazione ASC")
   	private Set<QualificaProfessionale> hasQualifica = new HashSet<QualificaProfessionale>();
   	
   	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_qualifica_professionale_giunta",insertable=true,updatable=true)
   	private QualificaProfessionale qualificaProfessionaleGiunta;
   	
   	@Column(name = "ordine_giunta")
    private Integer ordineGiunta = new Integer(0);
   	
   	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_qualifica_professionale_consiglio",insertable=true,updatable=true)
   	private QualificaProfessionale qualificaProfessionaleConsiglio;
   	
   	@Column(name = "ordine_consiglio")
    private Integer ordineConsiglio = new Integer(0);
   	
   	@Column(name = "future_enabled", columnDefinition = "tinyint(1) default 1")
	private Boolean futureEnabled;
   	
   	@Column(name = "valido_seduta_giunta", columnDefinition = "tinyint(1) default 0")
	private Boolean validoSedutaGiunta;
   	
   	@Column(name = "valido_seduta_consiglio", columnDefinition = "tinyint(1) default 0")
	private Boolean validoSedutaConsiglio;
   	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Boolean getPredefinito() {
		return predefinito;
	}

	public void setPredefinito(Boolean predefinito) {
		this.predefinito = predefinito;
	}

	public Validita getValidita() {
		return validita;
	}

	public void setValidita(Validita validita) {
		this.validita = validita;
	}

	public String getDelega() {
		return delega;
	}

	public void setDelega(String delega) {
		this.delega = delega;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public GruppoRuolo getGrupporuolo() {
		return grupporuolo;
	}

	public void setGrupporuolo(GruppoRuolo grupporuolo) {
		this.grupporuolo = grupporuolo;
	}
 

	public Set<QualificaProfessionale> getHasQualifica() {
		return hasQualifica;
	}

	public void setHasQualifica(Set<QualificaProfessionale> hasQualifica) {
		this.hasQualifica = hasQualifica;
	}
	
	public Set<TipoAtto> getTipiAtto() {
		return tipiAtto;
	}

	public void setTipiAtto(Set<TipoAtto> tipiAtto) {
		this.tipiAtto = tipiAtto;
	}
	
	public Integer getOrdineGiunta() {
		return ordineGiunta;
	}

	public void setOrdineGiunta(Integer ordineGiunta) {
		this.ordineGiunta = ordineGiunta;
	}
	
	public QualificaProfessionale getQualificaProfessionaleGiunta() {
		return qualificaProfessionaleGiunta;
	}

	public void setQualificaProfessionaleGiunta(QualificaProfessionale qualificaProfessionaleGiunta) {
		this.qualificaProfessionaleGiunta = qualificaProfessionaleGiunta;
	}
	
	public QualificaProfessionale getQualificaProfessionaleConsiglio() {
		return qualificaProfessionaleConsiglio;
	}

	public void setQualificaProfessionaleConsiglio(QualificaProfessionale qualificaProfessionaleConsiglio) {
		this.qualificaProfessionaleConsiglio = qualificaProfessionaleConsiglio;
	}

	public Integer getOrdineConsiglio() {
		return ordineConsiglio;
	}

	public void setOrdineConsiglio(Integer ordineConsiglio) {
		this.ordineConsiglio = ordineConsiglio;
	}

	public Boolean getValidoSedutaGiunta() {
		if (validoSedutaGiunta == null) {
			return false;
		}
		else {
			return validoSedutaGiunta;
		}
	}

	public void setValidoSedutaGiunta(Boolean validoSedutaGiunta) {
		this.validoSedutaGiunta = validoSedutaGiunta;
	}

	public Boolean getValidoSedutaConsiglio() {
		if (validoSedutaConsiglio == null) {
			return false;
		}
		else {
			return validoSedutaConsiglio;
		}
	}

	public void setValidoSedutaConsiglio(Boolean validoSedutaConsiglio) {
		this.validoSedutaConsiglio = validoSedutaConsiglio;
	}

	public Boolean getFutureEnabled() {
		return futureEnabled;
	}

	public void setFutureEnabled(Boolean futureEnabled) {
		this.futureEnabled = futureEnabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Profilo profilo = (Profilo) o;

		if (!Objects.equals(id, profilo.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Profilo{" + "id=" + id + ", descrizione='" + descrizione + "'"
				+ ", delega='" + delega + "'" + ", gruppoRuolo="+(grupporuolo!= null ?grupporuolo.getId():"NULL")+'}';
	}

	@Override
	public int compareTo(Profilo o) {
		return this.id.compareTo(o.getId());
	}


}

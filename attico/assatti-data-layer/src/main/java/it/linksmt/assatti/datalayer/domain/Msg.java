package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Un Msg.
 */
@Entity
@Table(name = "MSG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Msg implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Embedded
	private ValiditaMsg validita = new ValiditaMsg();

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "data_inserimento", nullable = false, updatable = false)
    private DateTime dataInserimento;
    
    @Size(max = 2500)
    @Column(name = "testo", length = 2500)
    private String testo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priorita")
    private PrioritaMsgEnum priorita;
    
    @ManyToMany
	@JoinTable(name = "MSG_AOO", joinColumns = {
			@JoinColumn(name = "msg_id", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "aoo_id", nullable = false) })
	private Set<Aoo> destinatari = new HashSet<Aoo>();
    
    @Column(name = "pubblicato_intranet")
	private Boolean pubblicatoIntranet;
    
    @Column(name = "pubblicato_internet")
	private Boolean pubblicatoInternet;
    
    @Transient
	@JsonProperty
	private Boolean letto;
    
    @ManyToOne
    private CategoriaMsg categoriaMsg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ValiditaMsg getValidita() {
		return validita;
	}

	public void setValidita(ValiditaMsg validita) {
		this.validita = validita;
	}

	public DateTime getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(DateTime dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public PrioritaMsgEnum getPriorita() {
		return priorita;
	}

	public void setPriorita(PrioritaMsgEnum priorita) {
		this.priorita = priorita;
	}

	public Set<Aoo> getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(Set<Aoo> destinatari) {
		this.destinatari = destinatari;
	}

	public Boolean getPubblicatoIntranet() {
		return pubblicatoIntranet;
	}

	public void setPubblicatoIntranet(Boolean pubblicatoIntranet) {
		this.pubblicatoIntranet = pubblicatoIntranet;
	}

	public Boolean getPubblicatoInternet() {
		return pubblicatoInternet;
	}

	public void setPubblicatoInternet(Boolean pubblicatoInternet) {
		this.pubblicatoInternet = pubblicatoInternet;
	}

	public Boolean getLetto() {
		return letto;
	}

	public void setLetto(Boolean letto) {
		this.letto = letto;
	}

	public CategoriaMsg getCategoriaMsg() {
		return categoriaMsg;
	}

	public void setCategoriaMsg(CategoriaMsg categoriaMsg) {
		this.categoriaMsg = categoriaMsg;
	}

}

package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Una ConfigurazioneRiversamento.
 */
@Entity
@Table(name = "configurazione_riversamento")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConfigurazioneRiversamento implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="tipodocumentoserie_id")
    private TipoDocumentoSerie tipoDocumentoSerie;
    
    @ManyToOne
	@JoinColumn(name="tipodocumento_id")
	private TipoDocumento tipoDocumento;
    
    @Column(name="tipoatto_id")
	private Long tipoAttoId;
    
    @Transient
    @JsonProperty
	private TipoAtto tipoAtto;
    
    @Transient
    @JsonProperty
	private Aoo aoo;
    
    @Column(name="aoo_id")
	private Long aooId;
    
    @Column(name="only_pubblicabili")
    private Boolean onlyPubblicabili;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "validodal")
    private LocalDate validoDal;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "validoal")
    private LocalDate validoAl;

	public Long getId() {
		return id;
	}

	public TipoDocumentoSerie getTipoDocumentoSerie() {
		return tipoDocumentoSerie;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public Long getAooId() {
		return aooId;
	}

	public Boolean getOnlyPubblicabili() {
		return onlyPubblicabili;
	}

	public LocalDate getValidoDal() {
		return validoDal;
	}

	public LocalDate getValidoAl() {
		return validoAl;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTipoDocumentoSerie(TipoDocumentoSerie tipoDocumentoSerie) {
		this.tipoDocumentoSerie = tipoDocumentoSerie;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public void setAooId(Long aooId) {
		this.aooId = aooId;
	}

	public void setOnlyPubblicabili(Boolean onlyPubblicabili) {
		this.onlyPubblicabili = onlyPubblicabili;
	}

	public void setValidoDal(LocalDate validoDal) {
		this.validoDal = validoDal;
	}

	public void setValidoAl(LocalDate validoAl) {
		this.validoAl = validoAl;
	}

	public Long getTipoAttoId() {
		return tipoAttoId;
	}

	public TipoAtto getTipoAtto() {
		return tipoAtto;
	}

	public void setTipoAttoId(Long tipoAttoId) {
		this.tipoAttoId = tipoAttoId;
	}

	public void setTipoAtto(TipoAtto tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aoo == null) ? 0 : aoo.hashCode());
		result = prime * result + ((aooId == null) ? 0 : aooId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((onlyPubblicabili == null) ? 0 : onlyPubblicabili.hashCode());
		result = prime * result + ((tipoDocumento == null) ? 0 : tipoDocumento.hashCode());
		result = prime * result + ((tipoDocumentoSerie == null) ? 0 : tipoDocumentoSerie.hashCode());
		result = prime * result + ((validoAl == null) ? 0 : validoAl.hashCode());
		result = prime * result + ((validoDal == null) ? 0 : validoDal.hashCode());
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
		ConfigurazioneRiversamento other = (ConfigurazioneRiversamento) obj;
		if (aoo == null) {
			if (other.aoo != null)
				return false;
		} else if (!aoo.equals(other.aoo))
			return false;
		if (aooId == null) {
			if (other.aooId != null)
				return false;
		} else if (!aooId.equals(other.aooId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (onlyPubblicabili == null) {
			if (other.onlyPubblicabili != null)
				return false;
		} else if (!onlyPubblicabili.equals(other.onlyPubblicabili))
			return false;
		if (tipoDocumento == null) {
			if (other.tipoDocumento != null)
				return false;
		} else if (!tipoDocumento.equals(other.tipoDocumento))
			return false;
		if (tipoDocumentoSerie == null) {
			if (other.tipoDocumentoSerie != null)
				return false;
		} else if (!tipoDocumentoSerie.equals(other.tipoDocumentoSerie))
			return false;
		if (validoAl == null) {
			if (other.validoAl != null)
				return false;
		} else if (!validoAl.equals(other.validoAl))
			return false;
		if (validoDal == null) {
			if (other.validoDal != null)
				return false;
		} else if (!validoDal.equals(other.validoDal))
			return false;
		return true;
	}
}

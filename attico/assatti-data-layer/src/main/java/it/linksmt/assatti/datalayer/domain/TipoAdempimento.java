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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A TipoAdempimento.
 */
@Entity
@Table(name = "TIPOADEMPIMENTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoAdempimento implements Serializable {

	public TipoAdempimento() {
	}

	public TipoAdempimento(Long id, String descrizione) {
		super();
		this.id = id;
		this.descrizione = descrizione;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "descrizione")
	private String descrizione;
	
	@Column(name = "beneficiario_required")
	private boolean beneficiarioRequired;
	
	@Column(name = "generazione_scheda_anagrafico_contabile")
	private Boolean generazioneSchedaAnagraficoContabile;

	@ManyToOne
	@JoinColumn(name="tipoiter_id",insertable=true,updatable=false)
	private TipoIter tipoiter;

	@Transient
	@JsonProperty
	private Boolean atti;
	
	public boolean isBeneficiarioRequired() {
		return beneficiarioRequired;
	}

	public void setBeneficiarioRequired(boolean beneficiarioRequired) {
		this.beneficiarioRequired = beneficiarioRequired;
	}

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

	public TipoIter getTipoiter() {
		return tipoiter;
	}

	public void setTipoiter(TipoIter tipoiter) {
		this.tipoiter = tipoiter;
	}

	public Boolean isGenerazioneSchedaAnagraficoContabile() {
		return generazioneSchedaAnagraficoContabile;
	}

	public void setGenerazioneSchedaAnagraficoContabile(
			Boolean generazioneSchedaAnagraficoContabile) {
		this.generazioneSchedaAnagraficoContabile = generazioneSchedaAnagraficoContabile;
	}
	
	public Boolean getAtti() {
		return atti;
	}

	public void setAtti(Boolean atti) {
		this.atti = atti;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TipoAdempimento tipoAdempimento = (TipoAdempimento) o;

		if (!Objects.equals(id, tipoAdempimento.id))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TipoAdempimento{" + "id=" + id + ", descrizione='"
				+ descrizione + "'" + '}';
	}
}

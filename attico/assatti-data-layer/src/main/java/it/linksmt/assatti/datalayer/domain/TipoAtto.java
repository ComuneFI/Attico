package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A TipoAtto.
 */
@Entity
@Table(name = "TIPOATTO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoAtto implements Serializable {

	private static final long serialVersionUID = 1L;

	public TipoAtto() {
	}
	public TipoAtto( final Long id ) {
		this.id=id;
	}

	public TipoAtto(final Long id, final String codice, final String descrizione) {
		this.id=id;
		this.codice=codice;
		this.descrizione=descrizione;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "codice")
	private String codice;

	@Column(name = "descrizione")
	private String descrizione;

	@Column(name = "processoBpmName")
	private String processoBpmName;
	
	@Column(name = "giorni_pubblicazione_albo")
	private Integer giorniPubblicazioneAlbo;
	
	@Column(name = "progressivo_proposta_aoo")
	private Boolean progressivoPropostaAoo;
	
	@Column(name = "progressivo_adozione_aoo")
	private Boolean progressivoAdozioneAoo;
	
	@ManyToOne
	@JoinColumn(name = "tipoprogressivo_id", insertable = true, updatable = true)
	private TipoProgressivo tipoProgressivo;
	
	@OneToMany(mappedBy = "tipoAtto", cascade={CascadeType.ALL}, orphanRemoval=true)
	private Set<Annullamento> statiAnnullamento = new HashSet<Annullamento>();
	
	@OneToMany(mappedBy = "tipoAtto"  )
	private Set<TipoIter> tipiIter = new HashSet<TipoIter>();
	
	@Column(name = "proponente")
	private Boolean proponente;
	
	@Column(name = "consiglio")
	private Boolean consiglio;
	
	@Column(name = "giunta")
	private Boolean giunta;
	
	@Column(name = "enabled")
	private Boolean enabled;
	
	@Column(name = "ricerca_colonne_nascoste")
	private String colonneNascosteRicerca;
	
	@Column(name = "ricerca_stato_concluso")
	private String statoConclusoRicerca;
	
	@OneToMany(mappedBy = "tipoAtto", fetch=FetchType.LAZY, cascade= {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<TipoAttoHasFaseRicerca> fasiRicerca = new HashSet<TipoAttoHasFaseRicerca>();
	
    @Column(name="modelloHtml_copia_non_conforme_id")
    private Long modelloHtmlCopiaNonConformeId;
	
	@Transient
	@JsonProperty
	private Boolean atti;
	
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(final String codice) {
		this.codice = codice;
	}

	public String getProcessoBpmName() {
		return processoBpmName;
	}

	public void setProcessoBpmName(final String processoBpmName) {
		this.processoBpmName = processoBpmName;
	}

	public Integer getGiorniPubblicazioneAlbo() {
		return giorniPubblicazioneAlbo;
	}
	
	public void setGiorniPubblicazioneAlbo(Integer giorniPubblicazioneAlbo) {
		this.giorniPubblicazioneAlbo = giorniPubblicazioneAlbo;
	}
	
	public Boolean getProgressivoPropostaAoo() {
		return progressivoPropostaAoo;
	}
	
	public void setProgressivoPropostaAoo(Boolean progressivoPropostaAoo) {
		this.progressivoPropostaAoo = progressivoPropostaAoo;
	}
	
	public Boolean getProgressivoAdozioneAoo() {
		return progressivoAdozioneAoo;
	}
	
	public void setProgressivoAdozioneAoo(Boolean progressivoAdozioneAoo) {
		this.progressivoAdozioneAoo = progressivoAdozioneAoo;
	}
	
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(final String descrizione) {
		this.descrizione = descrizione;
	}

	public Set<TipoIter> getTipiIter() {
		return tipiIter;
	}

	public void setTipiIter(final Set<TipoIter> tipiIter) {
		this.tipiIter = tipiIter;
	}

	public TipoProgressivo getTipoProgressivo() {
		return tipoProgressivo;
	}
	
	public void setTipoProgressivo(TipoProgressivo tipoProgressivo) {
		this.tipoProgressivo = tipoProgressivo;
	}
	
	public Set<Annullamento> getStatiAnnullamento() {
		return statiAnnullamento;
	}
	
	public void setStatiAnnullamento(Set<Annullamento> statiAnnullamento) {
		this.statiAnnullamento = statiAnnullamento;
	}
	
	public Boolean getAtti() {
		return atti;
	}
	
	public void setAtti(Boolean atti) {
		this.atti = atti;
	}
	
	public Boolean getProponente() {
		return proponente;
	}
	public void setProponente(Boolean proponente) {
		this.proponente = proponente;
	}
	
	

	public Boolean getConsiglio() {
		return consiglio;
	}
	public void setConsiglio(Boolean consiglio) {
		this.consiglio = consiglio;
	}
	public Boolean getGiunta() {
		return giunta;
	}
	public void setGiunta(Boolean giunta) {
		this.giunta = giunta;
	}
	public String getColonneNascosteRicerca() {
		return colonneNascosteRicerca;
	}
	public void setColonneNascosteRicerca(String colonneNascosteRicerca) {
		this.colonneNascosteRicerca = colonneNascosteRicerca;
	}
	public String getStatoConclusoRicerca() {
		return statoConclusoRicerca;
	}
	public void setStatoConclusoRicerca(String statoConclusoRicerca) {
		this.statoConclusoRicerca = statoConclusoRicerca;
	}
	public Set<TipoAttoHasFaseRicerca> getFasiRicerca() {
		return fasiRicerca;
	}
	public void setFasiRicerca(Set<TipoAttoHasFaseRicerca> fasiRicerca) {
		this.fasiRicerca = fasiRicerca;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Long getModelloHtmlCopiaNonConformeId() {
		return modelloHtmlCopiaNonConformeId;
	}
	public void setModelloHtmlCopiaNonConformeId(Long modelloHtmlCopiaNonConformeId) {
		this.modelloHtmlCopiaNonConformeId = modelloHtmlCopiaNonConformeId;
	}
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TipoAtto tipoAtto = (TipoAtto) o;

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
		return "TipoAtto{" + "id=" + id + ", codice='" + codice + "'"
				+ ", descrizione='" + descrizione + "'" + '}';
	}

}
